package plugins.vpcli.domain.vpmodelvisitor.element;

import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactoryFactory;

public class ViPackageFactory extends ViElementFactory {
    public ViPackageFactory(MyElementFactoryFactory myElementFactoryFactory) {
        super(myElementFactoryFactory);
    }

    public ViElement from(IModelElement vpElement) {
        return new ViPackage(vpElement, this, getMyElementFactory(vpElement));
    }

}
