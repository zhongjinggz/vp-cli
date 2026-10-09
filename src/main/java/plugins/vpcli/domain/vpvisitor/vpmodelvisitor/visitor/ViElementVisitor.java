package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor;

import plugins.vpcli.domain.vpvisitor.vpdiagramvisitor.element.ViDiagramElement;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.*;

import java.io.IOException;

public abstract class ViElementVisitor {
    private boolean lastOrNot = false;

    public void setLast(boolean lastOrNot) {
        this.lastOrNot = lastOrNot;
    }

    public boolean isLast() {
        return lastOrNot;
    }

    abstract public void visit(ViDiagramAsElement element) throws IOException;

    abstract public void visit(ViPackage element) throws IOException;

    abstract public void visit(ViModel element) throws IOException;

    abstract public void visit(ViClass element) throws IOException;

    abstract public void visit(ViUseCase element) throws IOException;

}
