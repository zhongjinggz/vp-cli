package plugins.vpcli.domain.myuml.mypackage;

import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElementPool;

public class MyModelFactory extends MyElementFactory {

    public MyModelFactory(MyElementPool myElementPool) {
        super(myElementPool);
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
