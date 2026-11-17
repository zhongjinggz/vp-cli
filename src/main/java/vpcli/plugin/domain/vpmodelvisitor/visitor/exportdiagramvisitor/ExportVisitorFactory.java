package vpcli.plugin.domain.vpmodelvisitor.visitor.exportdiagramvisitor;

import vpcli.plugin.domain.outputmodel.pumldiagram.PumlDiagramFactory;
import vpcli.plugin.domain.vpmodelvisitor.visitor.Visitor;
import vpcli.plugin.domain.vpmodelvisitor.visitor.VisitorFactory;
import vpcli.plugin.drivenadapter.DocWriter;
import vpcli.plugin.util.MyIO;

import java.io.File;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class ExportVisitorFactory implements VisitorFactory {

    private final PumlDiagramFactory pumlDiagramFactory;
    private final DocWriter docWriter;
    private final MyIO myIO;
    private File rootDir;

    @Inject
    public ExportVisitorFactory(
        PumlDiagramFactory pumlDiagramFactory,
        DocWriter docWriter,
        MyIO myIO
    ) {
        this.pumlDiagramFactory = pumlDiagramFactory;
        this.docWriter = docWriter;
        this.myIO = myIO;
    }

    @Override
    public Visitor build(Visitor preLevelVisitor, boolean thisLevelIsLast) {
        return new PracticalExportVisitor(
            ((ExportVisitor) preLevelVisitor).getPath(),
            pumlDiagramFactory,
            docWriter,
            myIO
        );
    }

    @Override
    public Visitor buildRoot() {
        return new RootExportVisitor(this.rootDir);
    }

    public void setRootDir(File dir) {
        this.rootDir = dir;
    }
}
