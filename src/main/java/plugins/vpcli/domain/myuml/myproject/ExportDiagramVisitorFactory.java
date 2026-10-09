package plugins.vpcli.domain.myuml.myproject;

import org.jetbrains.annotations.NotNull;
import plugins.vpcli.application.writers.WriterFactory;
import plugins.vpcli.domain.myuml.mydiagram1.MyDiagramFactory;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitor;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitorFactory;
import plugins.vpcli.drivenadapter.DirMaker;

import java.io.File;

public class ExportDiagramVisitorFactory implements ViElementVisitorFactory {

    private final MyDiagramFactory diagramFactory;
    private final WriterFactory writerFactory;
    private final MyProjectFactory myProjectFactory;
    private File rootDir;
    private final DirMaker dirMaker;

    public ExportDiagramVisitorFactory(
        DirMaker dirMaker,
        MyDiagramFactory diagramFactory,
        WriterFactory writerFactory,
        MyProjectFactory myProjectFactory) {

        this.dirMaker = dirMaker;
        this.diagramFactory = diagramFactory;
        this.writerFactory = writerFactory;
        this.myProjectFactory = myProjectFactory;
    }

    @Override
    public ViElementVisitor under(@NotNull ViElementVisitor higherVisitor) {

        var myProject = myProjectFactory.fromGlobal();

        ExportDiagramVisitor result = new ExportDiagramVisitor(
            (ExportDiagramVisitor) higherVisitor,
            this.dirMaker,
            diagramFactory,
            writerFactory,
            myProject
        );

        return result;

    }

    @Override
    public ViElementVisitor createRoot() {
        return new RootExportDiagramVisitor(this.rootDir);
    }

    public void setRootDir(File dir) {
        this.rootDir = dir;
    }
}
