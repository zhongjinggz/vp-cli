package plugins.vpcli.domain.vpstruct;

import java.io.IOException;
import java.util.List;

public abstract class VPStructElement {

    public VPStructElement() {
    }

    public void accept(VPVisitor visitor) throws IOException {
        visitor.visit(this);
    }

    ;

    public abstract String getName();

    public abstract ElementType getType();

    public abstract List<VPStructElement> getChildren(List<ElementType> elementTypes);

    public boolean typeIs(ElementType elementType) {
        return getType().equals(elementType);
    }
}
