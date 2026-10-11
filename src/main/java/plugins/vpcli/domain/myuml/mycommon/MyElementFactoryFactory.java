package plugins.vpcli.domain.myuml.mycommon;

import com.vp.plugin.model.factory.IModelElementFactory;
import plugins.vpcli.domain.myuml.mypackage.MyModelFactory;
import plugins.vpcli.domain.myuml.mypackage.MyPackageFactory;

public class MyElementFactoryFactory {
    public MyElementFactory of(String vpElementType) {
        switch (vpElementType) {
            case IModelElementFactory.MODEL_TYPE_PACKAGE:
                return new MyPackageFactory();
            case IModelElementFactory.MODEL_TYPE_MODEL:
                return new MyModelFactory();
            default:
                throw new IllegalArgumentException("Unknown element type: " + vpElementType);
        }
    }
}
