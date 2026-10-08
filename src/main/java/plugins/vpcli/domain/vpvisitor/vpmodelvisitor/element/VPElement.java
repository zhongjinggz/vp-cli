package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element;

import plugins.vpcli.domain.myuml.mycommon.ElementType;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.VPElementVisitor;

import java.io.IOException;
import java.util.List;

public abstract class VPElement {

    public VPElement() {
    }

    public void accept(VPElementVisitor visitor) throws IOException {
        visitor.visit(this);
    }

    ;

    public abstract String getName();

    public abstract ElementType getType();

    public abstract List<VPElement> getChildren(List<ElementType> elementTypes);

    public boolean typeIs(ElementType elementType) {
        return getType().equals(elementType);
    }
}
