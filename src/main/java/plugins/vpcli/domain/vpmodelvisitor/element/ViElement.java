package plugins.vpcli.domain.vpmodelvisitor.element;

import plugins.vpcli.domain.vpmodelvisitor.ViElementType;
import plugins.vpcli.domain.vpmodelvisitor.visitor.ViElementVisitor;

import java.io.IOException;
import java.util.List;

public abstract class ViElement {

    public ViElement() {
    }

    public abstract void accept(ViElementVisitor visitor) throws IOException;


    public abstract String getName();

    public abstract ViElementType getType();

    public abstract List<ViElement> getChildren(List<ViElementType> myElementTypes);

    public boolean typeIs(ViElementType viElementType) {
        return getType().equals(viElementType);
    }
}
