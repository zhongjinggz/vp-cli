package plugins.vpcli.domain.myuml.myproject;

import plugins.vpcli.application.writers.WriterFactory;
import plugins.vpcli.domain.myuml.mydiagram1.MyDiagramFactory;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitor;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitorFactory;
import plugins.vpcli.drivenadapter.FileFactory;
import plugins.vpcli.drivenadapter.FileIO;

import java.io.File;

public class ExportElementVisitorFactory1 implements ViElementVisitorFactory {

    private final FileIO fileIO;
    private final MyDiagramFactory diagramFactory;
    private final WriterFactory writerFactory;
    private File rootDir;
    private FileFactory fileFactory;

    public ExportElementVisitorFactory1(FileIO fileIO
        , MyDiagramFactory diagramFactory
        , WriterFactory writerFactory
        , FileFactory fileFactory) {

        this.fileIO = fileIO;
        this.diagramFactory = diagramFactory;
        this.writerFactory = writerFactory;
        this.fileFactory = fileFactory;
    }

    @Override
    public ViElementVisitor create(ViElementVisitor preLevelVisitor) {

        //TODO use NullObject pattern
        ExportElementVisitor preLevelExportVisitor = (preLevelVisitor == null ?
            null : (ExportElementVisitor) preLevelVisitor);

        ExportElementVisitor result = new ExportElementVisitor(
            preLevelExportVisitor
            , this.fileIO
            , diagramFactory
            , writerFactory
        );

        if (preLevelVisitor == null) {
            result.setParentDir(rootDir);
        }

        return result;

    }

    public void setRootDir(File dir) {
        this.rootDir = dir;
    }
}
