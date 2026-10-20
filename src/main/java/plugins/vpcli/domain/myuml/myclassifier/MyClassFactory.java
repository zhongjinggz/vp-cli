package plugins.vpcli.domain.myuml.myclassifier;

import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElementPool;
import plugins.vpcli.domain.myuml.mycommon.MyElementTypeRepository;
import plugins.vpcli.domain.myuml.mycommon.myelementtype.MyElementTypeClass;

public class MyClassFactory extends MyElementFactory {

    public MyClassFactory(MyElementPool myElementPool
//        , MyElementTypeClass myElementTypeClass
//        , MyElementTypeRepository myElementTypeRepository
    ) {
        // TODO use MyElementTypeClass.class instead
        super(myElementPool
        );
    }

    @Override
    public MyElement reallyCreate(MyElement parent, IModelElement vpElement) {
        return new MyClass(
            parent,
            vpElement.getId(),
            vpElement.getName(),
            myElementType,
            vpElement.getDescription()
        );
    }
}
