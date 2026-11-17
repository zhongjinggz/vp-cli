package vpcli.plugin.domain.vpmodelvisitor.visitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vpcli.plugin.domain.vpmodelvisitor.element.*;
import vpcli.plugin.domain.vpmodelvisitor.structure.ViProject;

public abstract class Visitor {
    Logger log = LoggerFactory.getLogger(Visitor.class);

    public void visit(ViDiagramAsElement element) {
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
