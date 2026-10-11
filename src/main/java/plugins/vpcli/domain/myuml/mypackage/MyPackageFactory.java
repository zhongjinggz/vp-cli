package plugins.vpcli.domain.myuml.mypackage;

import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;

public class MyPackageFactory extends MyElementFactory {

    @Override
    public MyPackage reallyCreate(MyElement parent, IModelElement vpElement) {
        return new MyPackage(
            parent,
            vpElement.getId(),
            vpElement.getName(),
            vpElement.getDescription());
    }
}
