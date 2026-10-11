package plugins.vpcli.domain.myuml.mypackage;

import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;

public class MyPackageFactory extends MyElementFactory {

    @Override
    public MyPackage create(MyElement parent, IModelElement vpElement) {
        return (MyPackage) getOrCreateElement(
            vpElement, parent,
            (ele, par) ->
                new MyPackage(
                    par,
                    ele.getId(),
                    ele.getName(),
                    ele.getDescription()
                )
        );
    }
}
