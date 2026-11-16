package vpcli.plugin.domain.vpmodelvisitor.element;

import com.vp.plugin.model.IModelElement;
import vpcli.plugin.domain.vpmodelvisitor.ViElementType;

public class ViModelElement {
    protected final IModelElement vpElement;

    public ViModelElement(IModelElement vpElement) {
        this.vpElement = vpElement;
    }

    public String getName() {
        return vpElement.getName();
    }

    public ViElementType getType() {
        return ViElementType.of(
            vpElement.getModelType());
    }

    public IModelElement getVPElement(){
        return vpElement;
    }
}
