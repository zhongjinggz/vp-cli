package plugins.vpcli.domain.myuml.myelement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plugins.vpcli.domain.myuml.myassociation.MyAssociationEndFactory;
import plugins.vpcli.domain.myuml.myassociation.MyAssociationFactory;
import plugins.vpcli.domain.myuml.myclassifier.MyClassFactory;
import plugins.vpcli.domain.myuml.mymodelrelationshipcontainer.MyModelRelationshipContainerFactory;
import plugins.vpcli.domain.myuml.mymodel.MyModelFactory;
import plugins.vpcli.domain.myuml.mypackage.MyPackageFactory;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotypeFactory;
import plugins.vpcli.domain.myuml.myunknownelement.UnimplementedElement;
import plugins.vpcli.domain.myuml.myunknownelement.UnimplementedElementFactory;
import plugins.vpcli.domain.myuml.myusecase.MyUseCaseFactory;

import java.util.HashMap;
import java.util.Map;

import static com.vp.plugin.model.factory.IModelElementFactory.*;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class MyElementFactoryFactory {
    private static final Logger log = LoggerFactory.getLogger(MyElementFactoryFactory.class);

    private final Map<String, MyElementFactory> factories = new HashMap<>();

    @Inject
    public MyElementFactoryFactory(
        MyPackageFactory myPackageFactory,
        MyClassFactory myClassFactory,
        MyModelFactory myModelFactory,
        MyUseCaseFactory myUseCaseFactory,
        MyAssociationFactory myAssociationFactory,
        MyAssociationEndFactory myAssociationEndFactory,
        MyModelRelationshipContainerFactory myModelRelationshipContainerFactory,
        MyStereotypeFactory myStereotypeFactory,
        UnimplementedElementFactory unimplementedElementFactory
    ) {
        register(MODEL_TYPE_PACKAGE, myPackageFactory);
        register(MODEL_TYPE_CLASS, myClassFactory);
        register(MODEL_TYPE_MODEL, myModelFactory);
        register(MODEL_TYPE_USE_CASE, myUseCaseFactory);
        register(MODEL_TYPE_ASSOCIATION, myAssociationFactory);
        register(MODEL_TYPE_ASSOCIATION_END, myAssociationEndFactory);
        register(MODEL_TYPE_MODEL_RELATIONSHIP_CONTAINER, myModelRelationshipContainerFactory);
        register(MODEL_TYPE_STEREOTYPE, myStereotypeFactory);
        register(UnimplementedElement.MODEL_TYPE_TEXT, unimplementedElementFactory);
    }

    public MyElementFactory of(String vpElementType) {
        MyElementFactory factory = factories.get(vpElementType);
        if (factory == null) {
            log.warn("Unimplemented element type {}", vpElementType);
            factory = factories.get(UnimplementedElement.MODEL_TYPE_TEXT);
        }
        return factory;
    }

    private void register(String modelType, MyElementFactory factory) {
        factory.setFactoryFactory(this);
        factories.put(modelType, factory);
    }
}
