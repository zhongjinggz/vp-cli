package plugins.vpcli.domain.vpstruct;

import java.io.IOException;

public abstract class VPVisitor {
    private boolean lastOrNot = false;

    public void setLast(boolean lastOrNot) {
        this.lastOrNot = lastOrNot;
    }

    public boolean isLast() {
        return lastOrNot;
    }

    abstract public void visit(VPStructElement vpStructElement) throws IOException;

}
