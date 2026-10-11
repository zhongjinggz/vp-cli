package plugins.vpcli.domain.myuml.myclassifier;

import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;

public class MyClassFactory extends MyElementFactory {

    @Override
    public MyElement reallyCreate(MyElement parent, IModelElement vpElement) {
        return new MyClass(
            parent,
            vpElement.getId(),
            vpElement.getName(),
            vpElement.getDescription()
        );
    }
}
