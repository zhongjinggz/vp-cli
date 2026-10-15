package plugins.vpcli.domain.myuml.myassociation;

import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElementPool;
import plugins.vpcli.domain.myuml.mycommon.myelementtype.MyElementTypeAssociation;
import plugins.vpcli.domain.myuml.mycommon.myelementtype.MyElementTypeUseCase;
import plugins.vpcli.domain.myuml.myusecase.MyUseCase;

public class MyAssociationFactory extends MyElementFactory {
    public MyAssociationFactory(
        MyElementPool myElementPool,
        MyElementTypeAssociation myElementType
    ) {
        super(myElementPool, myElementType);
    }

    @Override
    public MyAssociation reallyCreate(MyElement parent, IModelElement vpElement) {
        return new MyAssociation(
            parent,
            vpElement.getId(),
            vpElement.getName(),
            myElementType,
            vpElement.getDescription()
        );
    }
}
