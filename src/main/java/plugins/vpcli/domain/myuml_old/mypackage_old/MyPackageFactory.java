package plugins.vpcli.domain.myuml_old.mypackage_old;

import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElement_old2;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElementFactory;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElementPool;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;

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
    public MyPackage_old reallyCreate(MyElement_old2 parent, IModelElement vpElement, List<MyStereotype> myStereotypes) {
        return new MyPackage_old(
            parent,
            vpElement.getId(),
            vpElement.getName(),
            vpElement.getDescription(),
            myStereotypes);
    }
}
