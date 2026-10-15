package plugins.vpcli.domain.myuml.myproject;

import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagram;
import plugins.vpcli.domain.outputmodel.PumlDiagram;
import plugins.vpcli.domain.outputmodel.PumlDiagramFactory;
import plugins.vpcli.domain.vpvisitor.mydiagramfactory.MyDiagramFactory;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.*;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitor;
import plugins.vpcli.drivenadapter.DirMaker;
import plugins.vpcli.drivenadapter.DocWriter;

import java.io.File;
import java.io.IOException;

public class ExportDiagramVisitor extends ViElementVisitor {
    private final DirMaker makeDir;
    private final MyDiagramFactory buildMyDiagram;
    private final PumlDiagramFactory buildPlantUML;
    protected File path;
    private MyElement myElement;
    private DocWriter write;

    ExportDiagramVisitor(
        ExportDiagramVisitor higherVisitor,
        DirMaker dirMaker,
        MyDiagramFactory myDiagramFactory,
        PumlDiagramFactory pumlDiagramFactory,
        DocWriter docWriter) {
        super(higherVisitor);

        this.makeDir = dirMaker;
        this.buildMyDiagram = myDiagramFactory;
        this.buildPlantUML = pumlDiagramFactory;
        this.write = docWriter;
    }

    // used by RootExportDiagramVisitor only
    ExportDiagramVisitor(File path, MyProject myProject) {
        super(null);
        this.write = null;

        this.makeDir = null;
        this.buildMyDiagram = null;
        this.buildPlantUML = null;

        this.myElement = myProject;
        this.path = path;
    }

    File getPath() {
        return path;
    }

    @Override
    public void visit(ViDiagramAsElement viDiagram) throws IOException {
        // 暂时不将图表放入全局模型缓存，以便节省内存。未来视情况而定。
        var vpDiagram = viDiagram.getVPDiagramUIModel();
        MyDiagram myDiagram = buildMyDiagram.of(
            vpDiagram,
            ((ExportDiagramVisitor) higherVisitor).myElement
        );
        PumlDiagram puml = buildPlantUML.of(myDiagram);
        write.plantUML(puml, ((ExportDiagramVisitor) higherVisitor).getPath());
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


