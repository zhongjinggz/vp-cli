package plugins.vpcli.domain.myuml.mycommon;

import com.vp.plugin.model.factory.IModelElementFactory;
import plugins.vpcli.domain.myuml.myclassifier.MyClassFactory;
import plugins.vpcli.domain.myuml.mypackage.MyModelFactory;
import plugins.vpcli.domain.myuml.mypackage.MyPackageFactory;
import plugins.vpcli.domain.myuml.myusecase.MyUseCaseFactory;

public class MyElementFactoryFactory {
    private final MyPackageFactory myPackageFactory;
    private final MyClassFactory myClassFactory;
    private final MyModelFactory myModelFactory;
    private final MyUseCaseFactory myUseCaseFactory;

    public MyElementFactoryFactory(
        MyPackageFactory myPackageFactory,
        MyClassFactory myClassFactory,
        MyModelFactory myModelFactory,
        MyUseCaseFactory myUseCaseFactory
    ) {
        this.myPackageFactory = myPackageFactory;
        this.myClassFactory = myClassFactory;
        this.myModelFactory = myModelFactory;
        this.myUseCaseFactory = myUseCaseFactory;
    }

    public MyElementFactory of(String vpElementType) {
        switch (vpElementType) {
            case IModelElementFactory.MODEL_TYPE_PACKAGE:
                return myPackageFactory;
            case IModelElementFactory.MODEL_TYPE_MODEL:
                return myModelFactory;
            case IModelElementFactory.MODEL_TYPE_CLASS:
                return myClassFactory;
            case IModelElementFactory.MODEL_TYPE_USE_CASE:
                return myUseCaseFactory;
            default:
                throw new IllegalArgumentException("Unknown element type: " + vpElementType);
        }
    }
}
