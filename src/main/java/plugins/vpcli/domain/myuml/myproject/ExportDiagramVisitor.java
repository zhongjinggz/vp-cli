package plugins.vpcli.domain.myuml.myproject;

import plugins.vpcli.application.writers.ClassDiagramWriter;
import plugins.vpcli.application.writers.WriterFactory;
import plugins.vpcli.domain.myuml.mydiagram1.MyClassDiagram;
import plugins.vpcli.domain.myuml.mydiagram1.MyDiagramFactory;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.*;
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
        var diagramUIModel = element.getVPDiagramUIModel();
        MyClassDiagram myDiagram = diagramFactory.createClassDiagram(diagramUIModel);
        ClassDiagramWriter classDiagramWriter = writerFactory.createClassDiagramWriter(myDiagram);
        classDiagramWriter.write(this.parentDir);

    }

    @Override
    public void visit(ViPackage element) throws IOException {
        this.path = fileIO.makeDir
            .named(element.getName() + element.getType().getSuffix())
            .under(parentDir);
    }

    @Override
    public void visit(ViModel element) throws IOException {
        this.path = fileIO.makeDir
            .named(element.getName() + element.getType().getSuffix())
            .under(parentDir);
    }

    @Override
    public void visit(ViClass element) throws IOException {

    }

    @Override
    public void visit(ViUseCase element) throws IOException {

    }


    File getPath() {
        return path;
    }

}


