package plugins.vpcli.domain.myuml.myassociation;

import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElementPool;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class MyAssociationFactory extends MyElementFactory {
    @Inject
    public MyAssociationFactory(
        MyElementPool myElementPool
//        , MyElementTypeAssociation myElementType
//        , MyElementTypeRepository myElementTypeRepository
    ) {
        super(myElementPool
        );
    }

    @Override
    public MyAssociation reallyCreate(MyElement parent, IModelElement vpElement) {
        return new MyAssociation(
            parent,
            vpElement.getId(),
            vpElement.getName(),
//            myElementType,
            vpElement.getDescription()
        );
    }
}
