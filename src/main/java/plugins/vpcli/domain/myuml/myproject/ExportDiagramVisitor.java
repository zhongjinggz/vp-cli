package plugins.vpcli.domain.myuml.myproject;

import plugins.vpcli.application.writers_old.WriterFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagramFactory;
import plugins.vpcli.domain.outputmodel.MyDiagramToPumlConverter;
import plugins.vpcli.domain.outputmodel.PlantUMLDiagram;
import plugins.vpcli.domain.myuml.mycommon.MyElement_old;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagram;
import plugins.vpcli.domain.myuml.mydiagram_old.OldDiagramFactory;
import plugins.vpcli.domain.vpvisitor.vpdiagramvisitor.structure.VPToMyDiagramConverter;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.*;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitor;
import plugins.vpcli.drivenadapter.DirMaker;
import plugins.vpcli.drivenadapter.DocWriter;

import java.io.File;
import java.io.IOException;

public class ExportDiagramVisitor extends ViElementVisitor {
    private final DirMaker makeDir;
    private final MyDiagramFactory diagramFactory;
    private final WriterFactory writerFactory;
    private final File parentDir;
    private final MyProject myProject;
    private final ExportDiagramVisitor higherVisitor;
    private final VPToMyDiagramConverter convertToMyDiagram;
    private final MyDiagramToPumlConverter convertToPlantUML;

    protected File path;
    private MyElement myElement;
    private DocWriter write;

    ExportDiagramVisitor(
        ExportDiagramVisitor higherVisitor,
        DirMaker dirMaker,
        MyDiagramFactory diagramFactory,
        WriterFactory writerFactory,
        MyProject myProject,
        VPToMyDiagramConverter vpToMyDiagramConverter,
        MyDiagramToPumlConverter myDiagramToPumlConverter
    ) {
        this.myProject = myProject;
        this.diagramFactory = diagramFactory;
        this.writerFactory = writerFactory;
        this.makeDir = dirMaker;
        this.higherVisitor = higherVisitor;
        this.parentDir = higherVisitor.getPath();
        this.convertToMyDiagram = vpToMyDiagramConverter;
        this.convertToPlantUML = myDiagramToPumlConverter;
    }

    // used by RootExportDiagramVisitor only
    ExportDiagramVisitor(File path, MyProject myProject) {
        this.myProject = myProject;
        this.diagramFactory = null;
        this.writerFactory = null;
        this.makeDir = null;
        this.higherVisitor = null;
        this.parentDir = null;
        this.convertToMyDiagram = null;
        this.convertToPlantUML = null;

        this.myElement = myProject;
        this.path = path;
    }

    File getPath() {
        return path;
    }

    @Override
    public void visit(ViDiagramAsElement element) throws IOException {
//        // 暂时不将图表放入全局模型缓存，一遍节省内存。未来视情况而定。
//        var vpDiagram = element.getVPDiagramUIModel();
////        MyClassDiagram myDiagram = diagramFactory.createClassDiagram(diagramUIModel);
////        ClassDiagramWriter classDiagramWriter = writerFactory.createClassDiagramWriter(myDiagram);
////        classDiagramWriter.write(this.parentDir);
//        MyDiagram myDiagram = convertToMyDiagram.from(higherVisitor.myElement, vpDiagram);
//        PlantUMLDiagram puml = convertToPlantUML.from(myDiagram);
//        write.plantUML(puml);
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
        MyElement myElement = viPackage.toMyElement(higherVisitor.myElement);
        this.myElement = myElement;
    }

    private void makeDirOf(ViPackage viPackage) {
        this.path = makeDir
            .named(viPackage.getName() + viPackage.getType().getSuffix())
            .under(parentDir);
    }


}


