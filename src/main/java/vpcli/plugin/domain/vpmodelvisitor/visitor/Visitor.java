package vpcli.plugin.domain.vpmodelvisitor.visitor;

import vpcli.plugin.domain.vpmodelvisitor.element.*;

import java.io.IOException;

public abstract class Visitor {
    protected final Visitor higherVisitor;
    private boolean lastOrNot = false;

    protected Visitor(Visitor higherVisitor) {
        this.higherVisitor = higherVisitor;
    }

    public void setLast(boolean lastOrNot) {
        this.lastOrNot = lastOrNot;
    }

    public boolean isLast() {
        return lastOrNot;
    }

    abstract public void visit(ViDiagramAsElement element) throws IOException;

    abstract public void visit(ViPackage element) ;

    abstract public void visit(ViModel element) ;

    abstract public void visit(ViClass element) ;

    abstract public void visit(ViUseCase element) ;

}
