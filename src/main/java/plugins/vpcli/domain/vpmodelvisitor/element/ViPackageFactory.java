package plugins.vpcli.domain.vpmodelvisitor.element;

import com.vp.plugin.model.IModelElement;

public class ViPackageFactory extends ViElementFactory {
    public ViPackageFactory() {
        super();
    }

    public ViElement from(IModelElement vpElement) {
        return new ViPackage(vpElement, this);
    }

}
