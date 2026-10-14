package plugins.vpcli.domain.myuml.mypackage;

import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElementPool;
import plugins.vpcli.domain.myuml.mycommon.myelementtype.MyElementTypes;

public class MyPackageFactory extends MyElementFactory {

    public MyPackageFactory(MyElementPool myElementPool, MyElementTypes myElementTypes) {
        super(myElementPool, myElementTypes);
    }

    @Override
    public MyPackage reallyCreate(MyElement parent, IModelElement vpElement) {
        return new MyPackage(
            parent,
            vpElement.getId(),
            vpElement.getName(),
            myElementTypes.PACKAGE,
            vpElement.getDescription()
        );
    }
}
