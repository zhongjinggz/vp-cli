package plugins.vpcli.domain.myuml.mymodelrelationshipcontainer;

import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElementPool;

public class MyModelRelationshipContainerFactory extends MyElementFactory {
    public MyModelRelationshipContainerFactory(
        MyElementPool myElementPool
//        MyElementTypeModelRelationshipContainer myElementType
//        , MyElementTypeRepository myElementTypeRepository
    ) {
        super(myElementPool
        );
    }

    @Override
    public MyModelRelationshipContainer reallyCreate(MyElement parent, IModelElement vpElement) {
        return new MyModelRelationshipContainer(
            parent,
            vpElement.getId(),
            vpElement.getName(),
            vpElement.getDescription()
        );
    }
}
