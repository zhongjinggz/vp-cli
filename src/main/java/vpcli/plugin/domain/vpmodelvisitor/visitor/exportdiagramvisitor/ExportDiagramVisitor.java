package vpcli.plugin.domain.vpmodelvisitor.visitor.exportdiagramvisitor;

import org.jspecify.annotations.Nullable;
import vpcli.plugin.domain.outputmodel.pumldiagram.PumlDiagramFactory;
import vpcli.plugin.domain.vpmodelvisitor.element.*;
import vpcli.plugin.domain.vpmodelvisitor.structure.ViProject;
import vpcli.plugin.domain.vpmodelvisitor.visitor.Visitor;
import vpcli.plugin.drivenadapter.DocWriter;
import vpcli.plugin.util.MyIO;

import java.io.File;

public class ExportDiagramVisitor extends Visitor {
    private final PumlDiagramFactory pumlDiagramFactory ;
    @Nullable protected File path;
    @Nullable private final File preLevelPath;
    private final DocWriter write;
    private final MyIO myIO;

    ExportDiagramVisitor(
        @Nullable File preLevelPath,
        PumlDiagramFactory pumlDiagramFactory,
        DocWriter docWriter,
        MyIO myIO
    ) {
        super(null);

        this.pumlDiagramFactory = pumlDiagramFactory;
        this.write = docWriter;
        this.myIO = myIO;
        this.preLevelPath = preLevelPath;
    }

    // used by RootExportDiagramVisitor only
    @SuppressWarnings("NullAway")
    ExportDiagramVisitor(File path) {
        super(null);
        this.write = null;

        this.pumlDiagramFactory = null;

        this.path = path;
        this.myIO = null;
        this.preLevelPath = null;
    }

    File getPath() {
        return path;
    }

    @Override
    public void visit(ViDiagramAsElement viDiagram) {
        var vpDiagram = viDiagram.getVPDiagramUIModel();
        var puml = pumlDiagramFactory.build(vpDiagram);
        write.plantUML(puml, this.preLevelPath);
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

    @Override
    public void visit(ViProject viProject) {

    }

    private void makeDirOf(ViPackage viPackage) {

        File parentDir = this.preLevelPath;
        this.path = myIO.makeDir(
            parentDir,
            viPackage.getName() + viPackage.getType().getSuffix()
        );
    }


}
