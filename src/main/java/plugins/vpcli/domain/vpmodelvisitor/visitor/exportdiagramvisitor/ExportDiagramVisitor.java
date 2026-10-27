package plugins.vpcli.domain.vpmodelvisitor.visitor.exportdiagramvisitor;

import plugins.vpcli.domain.outputmodel.PumlDiagramFactory;
import plugins.vpcli.domain.vpmodelvisitor.element.*;
import plugins.vpcli.domain.vpmodelvisitor.visitor.ViElementVisitor;
import plugins.vpcli.drivenadapter.DocWriter;
import plugins.vpcli.util.MyIO;

import java.io.File;
import java.io.IOException;

public class ExportDiagramVisitor extends ViElementVisitor {
    private final PumlDiagramFactory buildPlantUML;
    protected File path;
    private final DocWriter write;
    private final MyIO myIO;

    ExportDiagramVisitor(
        ExportDiagramVisitor higherVisitor,
        PumlDiagramFactory pumlDiagramFactory,
        DocWriter docWriter,
        MyIO myIO
    ) {
        super(higherVisitor);

        this.buildPlantUML = pumlDiagramFactory;
        this.write = docWriter;
        this.myIO = myIO;
    }

    // used by RootExportDiagramVisitor only
    ExportDiagramVisitor(File path) {
        super(null);
        this.write = null;

        this.buildPlantUML = null;

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
        var puml = buildPlantUML.of(vpDiagram);
        write.plantUML(puml, ((ExportDiagramVisitor) higherVisitor).getPath());
    }

    @Override
    public void visit(ViPackage viPackage) {
        makeDirOf(viPackage);
    }

    @Override
    public void visit(ViModel viModel) {
        makeDirOf(viModel);
    }

    @Override
    public void visit(ViClass element) {

    }

    @Override
    public void visit(ViUseCase element) {

    }

    private void makeDirOf(ViPackage viPackage) {

        File parentDir = ((ExportDiagramVisitor) higherVisitor).getPath();
        this.path = myIO.makeDir(
            parentDir,
            viPackage.getName() + viPackage.getType().getSuffix()
        );
    }


}


