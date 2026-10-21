package plugins.vpcli.domain.myuml.mycommon;

import plugins.vpcli.domain.myuml.myassociation.MyAssociationFactory;
import plugins.vpcli.domain.myuml.myclassifier.MyClassFactory;
import plugins.vpcli.domain.myuml.mymodelrelationshipcontainer.MyModelRelationshipContainerFactory;
import plugins.vpcli.domain.myuml.mymodel.MyModelFactory;
import plugins.vpcli.domain.myuml.mypackage.MyPackageFactory;
import plugins.vpcli.domain.myuml.myusecase.MyUseCaseFactory;

import java.util.HashMap;
import java.util.Map;

import static com.vp.plugin.model.factory.IModelElementFactory.*;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.util.BugException;

@Singleton
public class MyElementFactoryFactory {
    private final Map<String, MyElementFactory> factories = new HashMap<>();

    @Inject
    public MyElementFactoryFactory(
        MyPackageFactory myPackageFactory,
        MyClassFactory myClassFactory,
        MyModelFactory myModelFactory,
        MyUseCaseFactory myUseCaseFactory,
        MyAssociationFactory myAssociationFactory,
        MyModelRelationshipContainerFactory myModelRelationshipContainerFactory,
        MyStereotypeFactory myStereotypeFactory
    ) {
        register(MODEL_TYPE_PACKAGE, myPackageFactory);
        register(MODEL_TYPE_CLASS, myClassFactory);
        register(MODEL_TYPE_MODEL, myModelFactory);
        register(MODEL_TYPE_USE_CASE, myUseCaseFactory);
        register(MODEL_TYPE_ASSOCIATION, myAssociationFactory);
        register(MODEL_TYPE_MODEL_RELATIONSHIP_CONTAINER, myModelRelationshipContainerFactory);
        register(MODEL_TYPE_STEREOTYPE, myStereotypeFactory);
    }

    public MyElementFactory of(String vpElementType) {
        MyElementFactory factory = factories.get(vpElementType);
        if (factory == null) {
            throw new BugException("Unknown element type: " + vpElementType);
        }
        return factory;
    }

    private void register(String modelType, MyElementFactory factory) {
        factory.setFactoryFactory(this);
        factories.put(modelType, factory);
    }
}
