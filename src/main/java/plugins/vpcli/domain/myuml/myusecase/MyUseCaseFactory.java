package plugins.vpcli.domain.myuml.myusecase;

import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElementPool;
import plugins.vpcli.domain.myuml.mycommon.myelementtype.MyElementTypes;

public class MyUseCaseFactory extends MyElementFactory {

    public MyUseCaseFactory(MyElementPool myElementPool, MyElementTypes myElementTypes) {
        super(myElementPool, myElementTypes);
    }

    @Override
    public MyUseCase reallyCreate(MyElement parent, IModelElement vpElement) {
        return new MyUseCase(
            parent,
            vpElement.getId(),
            vpElement.getName(),
            myElementTypes.USE_CASE,
            vpElement.getDescription()
        );
    }
}
