package plugins.vpcli.domain.myuml.mymodelrelationshipcontainer;

import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElementPool;
import plugins.vpcli.domain.myuml.mycommon.MyElementTypeRepository;
import plugins.vpcli.domain.myuml.mycommon.myelementtype.MyElementTypeModelRelationshipContainer;
import plugins.vpcli.domain.myuml.mycommon.myelementtype.MyElementTypePackage;
import plugins.vpcli.domain.myuml.mypackage.MyPackage;

public class MyModelRelationshipContainerFactory extends MyElementFactory {
    public MyModelRelationshipContainerFactory(
        MyElementPool myElementPool,
        MyElementTypeModelRelationshipContainer myElementType
        , MyElementTypeRepository myElementTypeRepository
    ) {
        super(myElementPool
            , myElementTypeRepository.MODEL_RELATIONSHIP_CONTAINER
            , myElementTypeRepository);
    }

    @Override
    public MyModelRelationshipContainer reallyCreate(MyElement parent, IModelElement vpElement) {
        return new MyModelRelationshipContainer(
            parent,
            vpElement.getId(),
            vpElement.getName(),
            myElementType,
            vpElement.getDescription()
        );
    }
}
