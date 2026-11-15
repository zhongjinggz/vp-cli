package vpcli.plugin.domain.vpmodelvisitor.visitor.exportdiagramvisitor;

//import org.jetbrains.annotations.NotNull;
import vpcli.plugin.domain.outputmodel.pumldiagram.PumlDiagramFactory;
import vpcli.plugin.domain.vpmodelvisitor.visitor.Visitor;
import vpcli.plugin.domain.vpmodelvisitor.visitor.VisitorFactory;
import vpcli.plugin.drivenadapter.DocWriter;
import vpcli.plugin.util.MyIO;

import java.io.File;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class ExportDiagramVisitorFactory implements VisitorFactory {

    private final PumlDiagramFactory pumlDiagramFactory;
    private final DocWriter docWriter;
    private final MyIO myIO;
    private File rootDir;

    @Inject
    public ExportDiagramVisitorFactory(
        PumlDiagramFactory pumlDiagramFactory,
        DocWriter docWriter,
        MyIO myIO
    ) {
        this.pumlDiagramFactory = pumlDiagramFactory;
        this.docWriter = docWriter;
        this.myIO = myIO;
    }

    @Override
    public Visitor under(Visitor higherVisitor) {
        return new ExportDiagramVisitor(
            (ExportDiagramVisitor) higherVisitor,
            pumlDiagramFactory,
            docWriter,
            myIO
        );

    }

    @Override
    public Visitor createRoot() {
        return new RootExportDiagramVisitor(this.rootDir);
    }

    public void setRootDir(File dir) {
        this.rootDir = dir;
    }
}
