package plugins.vpcli.domain.myuml.myproject;

import plugins.vpcli.application.writers_old.WriterCreator;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagram;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagramCreator;
import plugins.vpcli.domain.outputmodel.PlantUMLDiagram;
import plugins.vpcli.domain.outputmodel.ToPumlConverter;
import plugins.vpcli.domain.vpvisitor.vpdiagramvisitor.structure.ToMyDiagramConverter;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.*;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitor;
import plugins.vpcli.drivenadapter.DirMaker;
import plugins.vpcli.drivenadapter.DocWriter;

import java.io.File;
import java.io.IOException;

public class ExportDiagramVisitor extends ViElementVisitor {
    private final DirMaker makeDir;
    private final MyDiagramCreator diagramFactory;
    private final WriterCreator writerCreator;
    private final MyProject myProject;
    private final ToMyDiagramConverter convertToMyDiagram;
    private final ToPumlConverter convertToPlantUML;
    protected File path;
    private MyElement myElement;
    private DocWriter write;

    ExportDiagramVisitor(
        ExportDiagramVisitor higherVisitor,
        DirMaker dirMaker,
        MyDiagramCreator diagramFactory,
        WriterCreator writerCreator,
        MyProject myProject,
        ToMyDiagramConverter toMyDiagramConverter,
        ToPumlConverter toPumlConverter
    ) {
        super(higherVisitor);

        this.myProject = myProject;
        this.diagramFactory = diagramFactory;
        this.writerCreator = writerCreator;
        this.makeDir = dirMaker;
        this.convertToMyDiagram = toMyDiagramConverter;
        this.convertToPlantUML = toPumlConverter;
    }

    // used by RootExportDiagramVisitor only
    ExportDiagramVisitor(File path, MyProject myProject) {
        super(null);

        this.myProject = myProject;
        this.diagramFactory = null;
        this.writerCreator = null;
        this.makeDir = null;
        this.convertToMyDiagram = null;
        this.convertToPlantUML = null;

        this.myElement = myProject;
        this.path = path;
    }

    File getPath() {
        return path;
    }

    @Override
    public void visit(ViDiagramAsElement viDiagram) throws IOException {
//        // 暂时不将图表放入全局模型缓存，以便节省内存。未来视情况而定。
        var vpDiagram = viDiagram.getVPDiagramUIModel();
//        MyDiagram myDiagram = diagramFactory.create(((ExportDiagramVisitor)higherVisitor).myElement, vpDiagram);
////        ClassDiagramWriter classDiagramWriter = writerFactory.createClassDiagramWriter(myDiagram);
////        classDiagramWriter.write(this.parentDir);
        MyDiagram myDiagram = convertToMyDiagram.from(
            ((ExportDiagramVisitor) higherVisitor).myElement,
            vpDiagram
        );
        PlantUMLDiagram puml = convertToPlantUML.from(myDiagram);
        write.plantUML(puml);
    }

    @Override
    public void visit(ViPackage viPackage) throws IOException {
        buildElementModel(viPackage);
        makeDirOf(viPackage);
    }

    @Override
    public void visit(ViModel viModel) throws IOException {
        buildElementModel(viModel);
        makeDirOf(viModel);
    }

    @Override
    public void visit(ViClass element) throws IOException {

    }

    @Override
    public void visit(ViUseCase element) throws IOException {

    }

    private void buildElementModel(ViPackage viPackage) {
        MyElement myElement = viPackage.toMyElementUnder(
            ((ExportDiagramVisitor) higherVisitor).myElement);
        this.myElement = myElement;
    }

    private void makeDirOf(ViPackage viPackage) {

        File parentDir = ((ExportDiagramVisitor) higherVisitor).getPath();
        this.path = makeDir
            .named(viPackage.getName() + viPackage.getType().getSuffix())
            .under(parentDir);
    }


}


