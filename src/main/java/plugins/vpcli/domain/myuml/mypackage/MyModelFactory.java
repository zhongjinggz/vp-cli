package plugins.vpcli.domain.myuml.mypackage;

import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;

public class MyModelFactory extends MyElementFactory {

    @Override
    public MyModel create(MyElement parent, IModelElement vpElement) {
        return (MyModel) getOrCreateElement(
            vpElement, parent,
            (ele, par) ->
                new MyModel(
                    par,
                    ele.getId(),
                    ele.getName(),
                    ele.getDescription()
                )
        );
    }
}
