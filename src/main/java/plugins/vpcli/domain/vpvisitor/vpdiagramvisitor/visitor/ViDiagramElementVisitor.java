package plugins.vpcli.domain.vpvisitor.vpdiagramvisitor.visitor;

import plugins.vpcli.domain.vpvisitor.vpdiagramvisitor.element.ViDiagramElement;

import java.io.IOException;

public abstract class ViDiagramElementVisitor {
    private boolean lastOrNot = false;

    public void setLast(boolean lastOrNot) {
        this.lastOrNot = lastOrNot;
    }

    public boolean isLast() {
        return lastOrNot;
    }

    abstract public void visit(ViDiagramElement vpElement) throws IOException;

}
