package vpcli.plugin.domain.vpmodelvisitor.visitor.exportvisitor;

import vpcli.plugin.domain.outputmodel.pumldiagram.PumlDiagramFactory;
import vpcli.plugin.domain.vpmodelvisitor.element.viclassifier.ViClass;
import vpcli.plugin.domain.vpmodelvisitor.element.viclassifier.ViUseCase;
import vpcli.plugin.domain.vpmodelvisitor.element.vidiagram.ViDiagram;
import vpcli.plugin.domain.vpmodelvisitor.element.vipackage.ViModel;
import vpcli.plugin.domain.vpmodelvisitor.element.vipackage.ViPackage;
import vpcli.plugin.domain.vpmodelvisitor.element.viproject.ViProject;
import vpcli.plugin.drivenadapter.DocWriter;
import vpcli.plugin.util.MyIO;

import java.io.File;

public class PracticalExportVisitor extends ExportVisitor {
    private final PumlDiagramFactory pumlDiagramFactory ;
    private final File parentPath;
    private final DocWriter write;
    private final MyIO myIO;

    PracticalExportVisitor(
        File parentPath,
        PumlDiagramFactory pumlDiagramFactory,
        DocWriter docWriter,
        MyIO myIO
    ) {
        this.parentPath = parentPath;
        this.pumlDiagramFactory = pumlDiagramFactory;
        this.write = docWriter;
        this.myIO = myIO;
    }

    @Override
    public void visit(ViDiagram viDiagram) {
        var vpDiagram = viDiagram.getVPDiagramUIModel();
        var puml = pumlDiagramFactory.build(vpDiagram);
        write.plantUML(puml, this.parentPath);
    }

    @Override
    public void visit(ViPackage viPackage) {
        makeDirOf(viPackage);
    }

    @Override
    public void visit(ViModel viModel) {
        makeDirOf(viModel);
    }

    @Override
    public void visit(ViClass element) {

    }

    @Override
    public void visit(ViUseCase element) {

    }

    @Override
    public void visit(ViProject viProject) {

    }

    private void makeDirOf(ViPackage viPackage) {

        File parentDir = this.parentPath;
        this.path = myIO.makeDir(
            parentDir,
            viPackage.getName() + viPackage.getType().getSuffix()
        );
    }


}
