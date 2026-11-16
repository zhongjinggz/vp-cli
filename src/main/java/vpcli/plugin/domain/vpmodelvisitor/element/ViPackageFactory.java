package vpcli.plugin.domain.vpmodelvisitor.element;

import com.vp.plugin.model.IModelElement;

public class ViPackageFactory extends ViElementFactory {
    public ViPackageFactory() {
        super();
    }

    @Override
    public ViCompositeElement build(IModelElement vpElement) {
        return new ViPackage(vpElement, this);
    }

}
