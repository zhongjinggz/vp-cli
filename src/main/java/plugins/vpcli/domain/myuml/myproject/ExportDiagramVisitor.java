package plugins.vpcli.domain.myuml.myproject;

import plugins.vpcli.application.writers.ClassDiagramWriter;
import plugins.vpcli.application.writers.WriterFactory;
import plugins.vpcli.domain.myuml.mydiagram1.MyClassDiagram;
import plugins.vpcli.domain.myuml.mydiagram1.MyDiagramFactory;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.ViDiagramAsElement;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.ViElement;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.ViModelElement;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitor;
import plugins.vpcli.drivenadapter.FileIO;

import java.io.File;
import java.io.IOException;

public class ExportDiagramVisitor extends ViElementVisitor {
    private final FileIO fileIO;
    private final MyDiagramFactory diagramFactory;
    private final WriterFactory writerFactory;
    private final File parentDir;
    private final MyProject myProject;
    protected File path;

    ExportDiagramVisitor(
        ExportDiagramVisitor preLevelVisitor,
        FileIO fileIO,
        MyDiagramFactory diagramFactory,
        WriterFactory writerFactory,
        MyProject myProject) {

        this.fileIO = fileIO;
        this.diagramFactory = diagramFactory;
        this.writerFactory = writerFactory;
        this.parentDir = preLevelVisitor.getPath();
        this.myProject = myProject;
    }

    // used by RootExportDiagramVisitor only
    ExportDiagramVisitor(File path) {
        this.myProject = null;
        this.path = path;
        this.fileIO = null;
        this.diagramFactory = null;
        this.writerFactory = null;
        this.parentDir = null;
    }

//    @Override
//    public void visit(ViElement element) throws IOException {
//        switch (element.getType()) {
//            case PACKAGE:
//                visitPackage((ViModelElement) element);
//                break;
//            case MODEL:
//                visitModel((ViModelElement) element);
//                break;
//            case DIAGRAM:
//                visitClassDiagram((ViDiagramAsElement) element);
//                break;
//            default:
//                break;
//        }
//    }

    @Override
    public void visit(ViDiagramAsElement element) throws IOException {
        visitClassDiagram(element);

    }


    @Override
    public void visit(ViModelElement element) throws IOException {
        switch (element.getType()) {
            case PACKAGE:
                visitPackage(element);
                break;
            case MODEL:
                visitModel(element);
                break;
            default:
                break;
        }

    }

    private void visitPackage(ViModelElement element) {
        this.path = fileIO.makeDir
            .named(element.getName() + element.getType().getSuffix())
            .under(parentDir);
    }

    private void visitModel(ViModelElement element) {
        visitPackage(element);
    }

    private void visitClassDiagram(ViDiagramAsElement vpDiagram) throws IOException {
        var diagramUIModel = vpDiagram.getVPDiagramUIModel();
        MyClassDiagram myDiagram = diagramFactory.createClassDiagram(diagramUIModel);
        ClassDiagramWriter classDiagramWriter = writerFactory.createClassDiagramWriter(myDiagram);
        classDiagramWriter.write(this.parentDir);
    }

    File getPath() {
        return path;
    }

}


