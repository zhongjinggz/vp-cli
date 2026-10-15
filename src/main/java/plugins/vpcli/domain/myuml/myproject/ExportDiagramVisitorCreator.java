package plugins.vpcli.domain.myuml.myproject;

import org.jetbrains.annotations.NotNull;
import plugins.vpcli.domain.outputmodel.PumlDiagramFactory;
import plugins.vpcli.domain.vpvisitor.mydiagramfactory.MyDiagramFactory;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitor;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitorCreator;
import plugins.vpcli.drivenadapter.DirMaker;
import plugins.vpcli.drivenadapter.DocWriter;

import java.io.File;

public class ExportDiagramVisitorCreator implements ViElementVisitorCreator {

    private final MyProjectFactory myProjectFactory;
    private final DirMaker dirMaker;
    private final MyDiagramFactory myDiagramFactory;
    private final PumlDiagramFactory pumlDiagramFactory;
    private final DocWriter docWriter;
    private File rootDir;

    public ExportDiagramVisitorCreator(
        DirMaker dirMaker,
        MyProjectFactory myProjectFactory,
        MyDiagramFactory myDiagramFactory,
        PumlDiagramFactory pumlDiagramFactory,
        DocWriter docWriter
    ) {
        this.dirMaker = dirMaker;
        this.myProjectFactory = myProjectFactory;
        this.myDiagramFactory = myDiagramFactory;
        this.pumlDiagramFactory = pumlDiagramFactory;
        this.docWriter = docWriter;
    }

    @Override
    public ViElementVisitor under(@NotNull ViElementVisitor higherVisitor) {
        return new ExportDiagramVisitor(
            (ExportDiagramVisitor) higherVisitor,
            this.dirMaker,
            myDiagramFactory,
            pumlDiagramFactory,
            docWriter);

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
