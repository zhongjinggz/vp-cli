package plugins.vpcli.domain.myuml.mycommon;

import plugins.vpcli.domain.myuml.myassociation.MyAssociationFactory;
import plugins.vpcli.domain.myuml.myclassifier.MyClassFactory;
import plugins.vpcli.domain.myuml.mymodelrelationshipcontainer.MyModelRelationshipContainerFactory;
import plugins.vpcli.domain.myuml.mypackage.MyModelFactory;
import plugins.vpcli.domain.myuml.mypackage.MyPackageFactory;
import plugins.vpcli.domain.myuml.myusecase.MyUseCaseFactory;

import static com.vp.plugin.model.factory.IModelElementFactory.*;

public class MyElementFactoryFactory {
    // Map may be better than below fields
    private final MyPackageFactory myPackageFactory;
    private final MyClassFactory myClassFactory;
    private final MyModelFactory myModelFactory;
    private final MyUseCaseFactory myUseCaseFactory;
    private final MyAssociationFactory myAssociationFactory;
    private final MyModelRelationshipContainerFactory myModelRelationshipContainerFactory;

    public MyElementFactoryFactory(
        MyPackageFactory myPackageFactory,
        MyClassFactory myClassFactory,
        MyModelFactory myModelFactory,
        MyUseCaseFactory myUseCaseFactory,
        MyAssociationFactory myAssociationFactory,
        MyModelRelationshipContainerFactory myModelRelationshipContainerFactory
    ) {
        this.myPackageFactory = myPackageFactory;
        this.myPackageFactory.setFactoryFactory(this);

        this.myClassFactory = myClassFactory;
        this.myClassFactory.setFactoryFactory(this);

        this.myModelFactory = myModelFactory;
        this.myModelFactory.setFactoryFactory(this);

        this.myUseCaseFactory = myUseCaseFactory;
        this.myUseCaseFactory.setFactoryFactory(this);

        this.myAssociationFactory = myAssociationFactory;
        this.myAssociationFactory.setFactoryFactory(this);

        this.myModelRelationshipContainerFactory = myModelRelationshipContainerFactory;
        this.myModelRelationshipContainerFactory.setFactoryFactory(this);
    }

    public MyElementFactory of(String vpElementType) {
        switch (vpElementType) {
            case MODEL_TYPE_PACKAGE:
                return myPackageFactory;
            case MODEL_TYPE_MODEL:
                return myModelFactory;
            case MODEL_TYPE_CLASS:
                return myClassFactory;
            case MODEL_TYPE_USE_CASE:
                return myUseCaseFactory;
            case MODEL_TYPE_ASSOCIATION:
                return myAssociationFactory;
            case MODEL_TYPE_MODEL_RELATIONSHIP_CONTAINER:
                return myModelRelationshipContainerFactory;
            default:
                throw new IllegalArgumentException("Unknown element type: " + vpElementType);
        }
    }
}
