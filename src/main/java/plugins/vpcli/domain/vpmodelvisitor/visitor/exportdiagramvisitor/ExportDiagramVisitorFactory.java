package plugins.vpcli.domain.vpmodelvisitor.visitor.exportdiagramvisitor;

import org.jetbrains.annotations.NotNull;
import plugins.vpcli.domain.outputmodel.PumlDiagramFactory;
import plugins.vpcli.domain.vpmodelvisitor.visitor.ViElementVisitor;
import plugins.vpcli.domain.vpmodelvisitor.visitor.ViElementVisitorFactory;
import plugins.vpcli.drivenadapter.DocWriter;
import plugins.vpcli.util.MyIO;

import java.io.File;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class ExportDiagramVisitorFactory implements ViElementVisitorFactory {

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
    public ViElementVisitor under(@NotNull ViElementVisitor higherVisitor) {
        return new ExportDiagramVisitor(
            (ExportDiagramVisitor) higherVisitor,
            pumlDiagramFactory,
            docWriter,
            myIO
        );

    }

    @Override
    public ViElementVisitor createRoot() {
        return new RootExportDiagramVisitor(this.rootDir);
    }

    public void setRootDir(File dir) {
        this.rootDir = dir;
    }
}
