package plugins.vpcli.domain.myuml.myproject;

import plugins.vpcli.application.writers.WriterFactory;
import plugins.vpcli.domain.mydiagram.MyDiagramFactory;
import plugins.vpcli.domain.vpstruct.VPVisitor;
import plugins.vpcli.domain.vpstruct.VPVisitorFactory;
import plugins.vpcli.drivenadapter.FileFactory;
import plugins.vpcli.drivenadapter.FileIO;

import java.io.File;

public class ExportVisitorFactory implements VPVisitorFactory {

    private final FileIO fileIO;
    private final MyDiagramFactory diagramFactory;
    private final WriterFactory writerFactory;
    private File rootDir;
    private FileFactory fileFactory;

    public ExportVisitorFactory(FileIO fileIO
        , MyDiagramFactory diagramFactory
        , WriterFactory writerFactory
        , FileFactory fileFactory) {

        this.fileIO = fileIO;
        this.diagramFactory = diagramFactory;
        this.writerFactory = writerFactory;
        this.fileFactory = fileFactory;
    }

    @Override
    public VPVisitor create(VPVisitor preLevelVisitor) {

        //TODO use NullObject pattern
        ExportVisitor preLevelExportVisitor = (preLevelVisitor == null ?
            null : (ExportVisitor) preLevelVisitor);

        ExportVisitor result = new ExportVisitor(
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
