package plugins.vpcli.domain.vpmodelvisitor.element;

import plugins.vpcli.domain.myuml.mycommon.MyElementType;
import plugins.vpcli.domain.vpmodelvisitor.visitor.ViElementVisitor;

import java.io.IOException;
import java.util.List;

public abstract class ViElement {

    public ViElement() {
    }

    public abstract void accept(ViElementVisitor visitor) throws IOException;


    public abstract String getName();

    public abstract MyElementType getType();

    public abstract List<ViElement> getChildren(List<MyElementType> myElementTypes);

    public boolean typeIs(MyElementType myElementType) {
        return getType().equals(myElementType);
    }
}
