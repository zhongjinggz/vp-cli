package plugins.vpcli.domain.myuml.myclassifier;

import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;
import plugins.vpcli.domain.myuml.mypackage.MyModel;

public class MyClassFactory extends MyElementFactory {

    @Override
    public MyClass create(MyElement parent, IModelElement vpElement) {
        return (MyClass) getOrCreateElement(
            vpElement, parent,
            (ele, par) ->
                new MyClass(
                    par,
                    ele.getId(),
                    ele.getName(),
                    ele.getDescription()
                )
        );
    }
}
