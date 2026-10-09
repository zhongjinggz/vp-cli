package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element;

import plugins.vpcli.domain.myuml.mycommon.ElementType;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitor;

import java.io.IOException;
import java.util.List;

public abstract class ViElement {

    public ViElement() {
    }

    //    public void accept(ViElementVisitor visitor) throws IOException {
//        visitor.visit(this);
//    }
    public abstract void accept(ViElementVisitor visitor) throws IOException;


    public abstract String getName();

    public abstract ElementType getType();

    public abstract List<ViElement> getChildren(List<ElementType> elementTypes);

    public boolean typeIs(ElementType elementType) {
        return getType().equals(elementType);
    }
}
