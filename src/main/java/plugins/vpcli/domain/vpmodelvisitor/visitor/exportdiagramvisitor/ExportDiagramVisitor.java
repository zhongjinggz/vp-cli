package plugins.vpcli.domain.vpmodelvisitor.visitor.exportdiagramvisitor;

import plugins.vpcli.domain.myuml.myelement.MyElement;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagram;
import plugins.vpcli.domain.myuml.myproject.MyProject;
import plugins.vpcli.domain.outputmodel.PumlDiagram;
import plugins.vpcli.domain.outputmodel.PumlDiagramFactory;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagramFactory;
import plugins.vpcli.domain.vpmodelvisitor.element.*;
import plugins.vpcli.domain.vpmodelvisitor.visitor.ViElementVisitor;
import plugins.vpcli.drivenadapter.DocWriter;
import plugins.vpcli.util.MyIO;

import java.io.File;
import java.io.IOException;

public class ExportDiagramVisitor extends ViElementVisitor {
    private final MyDiagramFactory buildMyDiagram;
    private final PumlDiagramFactory buildPlantUML;
    protected File path;
    private MyElement myElement;
    private final DocWriter write;
    private final MyIO myIO;

    ExportDiagramVisitor(
        ExportDiagramVisitor higherVisitor,
        MyDiagramFactory myDiagramFactory,
        PumlDiagramFactory pumlDiagramFactory,
        DocWriter docWriter,
        MyIO myIO
    ) {
        super(higherVisitor);

        this.buildMyDiagram = myDiagramFactory;
        this.buildPlantUML = pumlDiagramFactory;
        this.write = docWriter;
        this.myIO = myIO;
    }

    // used by RootExportDiagramVisitor only
    ExportDiagramVisitor(File path, MyProject myProject) {
        super(null);
        this.write = null;

        this.buildMyDiagram = null;
        this.buildPlantUML = null;

        this.myElement = myProject;
        this.path = path;
        this.myIO = null;
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
    public void visit(ViPackage viPackage) {
        buildElementModel(viPackage);
        makeDirOf(viPackage);
    }

    @Override
    public void visit(ViModel viModel) {
        buildElementModel(viModel);
        makeDirOf(viModel);
    }

    @Override
    public void visit(ViClass element) {

    }

    @Override
    public void visit(ViUseCase element) {

    }

    private void buildElementModel(ViPackage viPackage) {
        MyElement myElement = viPackage.toMyElementUnder(
            ((ExportDiagramVisitor) higherVisitor).myElement);
        this.myElement = myElement;
    }

    private void makeDirOf(ViPackage viPackage) {

        File parentDir = ((ExportDiagramVisitor) higherVisitor).getPath();
        this.path = myIO.makeDir(
            parentDir,
            viPackage.getName() + viPackage.getType().getSuffix()
        );
    }


}


