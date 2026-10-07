package plugins.vpcli.domain.myuml.myproject;

import plugins.vpcli.application.writers.ClassDiagramWriter;
import plugins.vpcli.application.writers.WriterFactory;
import plugins.vpcli.domain.mydiagram.MyClassDiagram;
import plugins.vpcli.domain.mydiagram.MyDiagramFactory;
import plugins.vpcli.domain.vpstruct.VPDiagram;
import plugins.vpcli.domain.vpstruct.VPStructElement;
import plugins.vpcli.domain.vpstruct.VPVisitor;
import plugins.vpcli.drivenadapter.FileIO;

import java.io.File;
import java.io.IOException;

public class ExportVisitor extends VPVisitor {
    private final FileIO fileIO;
    private final MyDiagramFactory diagramFactory;
    private final WriterFactory writerFactory;
    private File path;
    private File parentDir;

    public ExportVisitor(
        ExportVisitor preLevelVisitor
        , FileIO fileIO
        , MyDiagramFactory diagramFactory
        , WriterFactory writerFactory) {

        this.fileIO = fileIO;
        this.diagramFactory = diagramFactory;
        this.writerFactory = writerFactory;
        if (preLevelVisitor != null) {
            this.parentDir = preLevelVisitor.getPath();
        }
    }

    public void setParentDir(File dir) {
        this.parentDir = dir;

    }

    @Override
    public void visit(VPStructElement element) throws IOException {
        switch (element.getType()) {
            case PACKAGE:
                visitPackage(element);
                break;
            case MODEL:
                visitModel(element);
                break;
            case CLASS_DIAGRAM:
                visitClassDiagram(element);
                break;
            default:
                break;
        }
    }

    private void visitPackage(VPStructElement element) {
        this.path = fileIO.makeDir
            .named(element.getName() + element.getType().getSuffix())
            .under(parentDir);
    }

    private void visitModel(VPStructElement element) {
        visitPackage(element);
    }

    private void visitClassDiagram(VPStructElement structElement) throws IOException {
        var vpDiagram = (VPDiagram) structElement;
        var diagramUIModel = vpDiagram.getVPDiagramUIModel();
        MyClassDiagram diagram = diagramFactory.createClassDiagram(diagramUIModel);
        ClassDiagramWriter classDiagramWriter = writerFactory.createClassDiagramWriter(diagram);
        classDiagramWriter.write(this.parentDir);
    }

    File getPath() {
        return path;
    }

}


