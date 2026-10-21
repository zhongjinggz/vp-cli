package plugins.vpcli.domain.myuml.mypackage;

import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElementPool;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.myuml.mycommon.MyStereotype;

import java.util.List;

@Singleton
public class MyPackageFactory extends MyElementFactory {

    @Inject
    public MyPackageFactory(MyElementPool myElementPool
//        , MyElementTypePackage myElementType
//        , MyElementTypeRepository myElementTypeRepository
    ) {
        super(myElementPool
        );
    }

    @Override
    public MyPackage reallyCreate(MyElement parent, IModelElement vpElement, List<MyStereotype> myStereotypes) {
        return new MyPackage(
            parent,
            vpElement.getId(),
            vpElement.getName(),
            vpElement.getDescription(),
            myStereotypes);
    }
}
