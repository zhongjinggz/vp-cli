package plugins.vpcli.domain.vpstruct;

import com.vp.plugin.model.IModelElement;

public class VPStructElement {
    private final IModelElement vpElement;

    public VPStructElement(IModelElement vpElement) {
        this.vpElement = vpElement;
    }

    public void accept(VPVisitor visitor){
        visitor.visit(this);
    };

    public String getName() {
        return vpElement.getName();
    }
    
    public String getType() {
        return vpElement.getModelType();
    }
}
