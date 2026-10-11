package plugins.vpcli.domain.myuml.mycommon;

import com.vp.plugin.model.factory.IModelElementFactory;
import plugins.vpcli.domain.myuml.myclassifier.MyClassFactory;
import plugins.vpcli.domain.myuml.mypackage.MyModelFactory;
import plugins.vpcli.domain.myuml.mypackage.MyPackageFactory;
import plugins.vpcli.domain.myuml.myusecase.MyUseCaseFactory;

public class MyElementFactoryFactory {
    public MyElementFactory of(String vpElementType) {
        switch (vpElementType) {
            case IModelElementFactory.MODEL_TYPE_PACKAGE:
                return new MyPackageFactory();
            case IModelElementFactory.MODEL_TYPE_MODEL:
                return new MyModelFactory();
            case IModelElementFactory.MODEL_TYPE_CLASS:
                return new MyClassFactory();
            case IModelElementFactory.MODEL_TYPE_USE_CASE:
                return new MyUseCaseFactory();
            default:
                throw new IllegalArgumentException("Unknown element type: " + vpElementType);
        }
    }
}
