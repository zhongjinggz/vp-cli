package plugins.vpcli.domain.myuml.mymodel;

import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElementPool;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class MyModelFactory extends MyElementFactory {

    @Inject
    public MyModelFactory(MyElementPool myElementPool
//        , MyElementTypeModel myElementType
//        , MyElementTypeRepository myElementTypeRepository
    ) {
        super(myElementPool
        );
    }

    @Override
    public MyElement reallyCreate(MyElement parent, IModelElement vpElement) {
        return new MyModel(
            parent,
            vpElement.getId(),
            vpElement.getName(),
            vpElement.getDescription()
        );
    }
}