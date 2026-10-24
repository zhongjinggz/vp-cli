package plugins.vpcli.domain.myuml.mymodelrelationshipcontainer;

import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.myelement.MyElement;
import plugins.vpcli.domain.myuml.myelement.MyElementFactory;
import plugins.vpcli.domain.myuml.myelement.MyElementPool;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;

import java.util.List;

@Singleton
public class MyModelRelationshipContainerFactory extends MyElementFactory {
    @Inject
    public MyModelRelationshipContainerFactory(
        MyElementPool myElementPool
//        MyElementTypeModelRelationshipContainer myElementType
//        , MyElementTypeRepository myElementTypeRepository
    ) {
        super(myElementPool
        );
    }

    @Override
    public MyModelRelationshipContainer reallyCreate(MyElement parent, IModelElement vpElement, List<MyStereotype> myStereotypes) {
        return new MyModelRelationshipContainer(
            parent,
            vpElement.getId(),
            vpElement.getName(),
            vpElement.getDescription()
        );
    }
}
