package vpcli.plugin.domain.vpmodelvisitor.visitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vpcli.plugin.domain.vpmodelvisitor.element.fundation.ViUnimplemented;
import vpcli.plugin.domain.vpmodelvisitor.element.viclassifier.ViClass;
import vpcli.plugin.domain.vpmodelvisitor.element.viclassifier.ViNary;
import vpcli.plugin.domain.vpmodelvisitor.element.viclassifier.ViUseCase;
import vpcli.plugin.domain.vpmodelvisitor.element.vidiagram.ViDiagram;
import vpcli.plugin.domain.vpmodelvisitor.element.vipackage.ViModel;
import vpcli.plugin.domain.vpmodelvisitor.element.vipackage.ViPackage;
import vpcli.plugin.domain.vpmodelvisitor.element.viproject.ViProject;

public abstract class Visitor {
    Logger log = LoggerFactory.getLogger(Visitor.class);

    public void visit(ViDiagram element) {
    }

    public void visit(ViPackage element) {
    }

    public void visit(ViModel element) {
    }

    public void visit(ViClass element) {
    }

    public void visit(ViUseCase element) {
    }

    public void visit(ViProject viProject) {
    }

    public void visit(ViUnimplemented viUnimplemented) {
    }

    public void visit(ViNary viNary) {

    }
}
