package plugins.vpcli.domain.vpmodelvisitor.visitor.exportdiagramvisitor;

import org.jetbrains.annotations.NotNull;
import plugins.vpcli.domain.myuml.myproject.MyProjectFactory;
import plugins.vpcli.domain.outputmodel.PumlDiagramFactory;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagramFactory;
import plugins.vpcli.domain.vpmodelvisitor.visitor.ViElementVisitor;
import plugins.vpcli.domain.vpmodelvisitor.visitor.ViElementVisitorFactory;
import plugins.vpcli.drivenadapter.DocWriter;
import plugins.vpcli.util.MyIO;

import java.io.File;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class ExportDiagramVisitorFactory implements ViElementVisitorFactory {

    private final MyProjectFactory myProjectFactory;
    private final MyDiagramFactory myDiagramFactory;
    private final PumlDiagramFactory pumlDiagramFactory;
    private final DocWriter docWriter;
    private final MyIO myIO;
    private File rootDir;

    @Inject
    public ExportDiagramVisitorFactory(
        MyProjectFactory myProjectFactory,
        MyDiagramFactory myDiagramFactory,
        PumlDiagramFactory pumlDiagramFactory,
        DocWriter docWriter,
        MyIO myIO
    ) {
        this.myProjectFactory = myProjectFactory;
        this.myDiagramFactory = myDiagramFactory;
        this.pumlDiagramFactory = pumlDiagramFactory;
        this.docWriter = docWriter;
        this.myIO = myIO;
    }

    @Override
    public ViElementVisitor under(@NotNull ViElementVisitor higherVisitor) {
        return new ExportDiagramVisitor(
            (ExportDiagramVisitor) higherVisitor,
            myDiagramFactory,
            pumlDiagramFactory,
            docWriter,
            myIO
        );

    }

    @Override
    public ViElementVisitor createRoot() {
        var myProject = myProjectFactory.fromGlobal();
        return new RootExportDiagramVisitor(this.rootDir, myProject);
    }

    public void setRootDir(File dir) {
        this.rootDir = dir;
    }
}
