package vpcli.plugin.domain.vpmodelvisitor.visitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vpcli.plugin.domain.vpmodelvisitor.element.*;
import vpcli.plugin.domain.vpmodelvisitor.structure.ViProject;

import java.io.IOException;

public abstract class Visitor {
    Logger log = LoggerFactory.getLogger(Visitor.class);

    protected final Visitor higherVisitor;
    private boolean lastOrNot = false;

    protected Visitor(Visitor higherVisitor) {
        this.higherVisitor = higherVisitor;
    }

    public void setLast(boolean lastOrNot) {
        this.lastOrNot = lastOrNot;
    }

    //TODO To be removed
    public boolean isLast() {
        return lastOrNot;
    }

    abstract public void visit(ViDiagramAsElement element) ;

    abstract public void visit(ViPackage element) ;

    abstract public void visit(ViModel element) ;

    abstract public void visit(ViClass element) ;

    abstract public void visit(ViUseCase element) ;

    abstract public void visit(ViProject viProject);

    public void visit(ViUnimplemented viUnimplemented) {
    }

    public void visit(ViNary viNary) {

    }
}
