package plugins.vpcli.domain.myuml.myproject;

import plugins.vpcli.application.writers.ClassDiagramWriter;
import plugins.vpcli.application.writers.WriterFactory;
import plugins.vpcli.domain.myuml.mydiagram.MyClassDiagram;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagramFactory;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.ViDiagramAsElement;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.ViElement;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitor;
import plugins.vpcli.drivenadapter.FileIO;

import java.io.File;
import java.io.IOException;

public class ExportElementVisitor extends ViElementVisitor {
    private final FileIO fileIO;
    private final MyDiagramFactory diagramFactory;
    private final WriterFactory writerFactory;
    private File path;
    private File parentDir;

    public ExportElementVisitor(
        ExportElementVisitor preLevelVisitor
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
    public void visit(ViElement element) throws IOException {
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

    private void visitPackage(ViElement element) {
        this.path = fileIO.makeDir
            .named(element.getName() + element.getType().getSuffix())
            .under(parentDir);
    }

    private void visitModel(ViElement element) {
        visitPackage(element);
    }

    private void visitClassDiagram(ViElement structElement) throws IOException {
        var vpDiagram = (ViDiagramAsElement) structElement;
        var diagramUIModel = vpDiagram.getVPDiagramUIModel();
        MyClassDiagram myDiagram = diagramFactory.createClassDiagram(diagramUIModel);
        ClassDiagramWriter classDiagramWriter = writerFactory.createClassDiagramWriter(myDiagram);
        classDiagramWriter.write(this.parentDir);
    }

    File getPath() {
        return path;
    }

}


