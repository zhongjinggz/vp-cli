package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor;

import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.ViElement;

import java.io.IOException;

public abstract class ViElementVisitor {
    private boolean lastOrNot = false;

    public void setLast(boolean lastOrNot) {
        this.lastOrNot = lastOrNot;
    }

    public boolean isLast() {
        return lastOrNot;
    }

    abstract public void visit(ViElement viElement) throws IOException;

}
