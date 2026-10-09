package plugins.vpcli.domain.myuml.myproject;

import org.jetbrains.annotations.NotNull;
import plugins.vpcli.application.writers.WriterFactory;
import plugins.vpcli.domain.myuml.mydiagram1.MyDiagramFactory;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitor;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitorFactory;
import plugins.vpcli.drivenadapter.FileFactory;
import plugins.vpcli.drivenadapter.FileIO;

import java.io.File;

public class ExportDiagramVisitorFactory implements ViElementVisitorFactory {

    private final FileIO fileIO;
    private final MyDiagramFactory diagramFactory;
    private final WriterFactory writerFactory;
    private File rootDir;
    private FileFactory fileFactory;

    public ExportDiagramVisitorFactory(FileIO fileIO
        , MyDiagramFactory diagramFactory
        , WriterFactory writerFactory
        , FileFactory fileFactory) {

        this.fileIO = fileIO;
        this.diagramFactory = diagramFactory;
        this.writerFactory = writerFactory;
        this.fileFactory = fileFactory;
    }

    @Override
    public ViElementVisitor under(@NotNull ViElementVisitor higherVisitor) {

        //TODO use NullObject pattern
        ExportDiagramVisitor preLevelExportVisitor = (higherVisitor == null ?
            null : (ExportDiagramVisitor) higherVisitor);

        ExportDiagramVisitor result = new ExportDiagramVisitor(
            preLevelExportVisitor
            , this.fileIO
            , diagramFactory
            , writerFactory
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
