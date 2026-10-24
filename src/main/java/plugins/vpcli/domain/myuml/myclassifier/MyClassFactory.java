package plugins.vpcli.domain.myuml.myclassifier;

import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.myelement.MyElement;
import plugins.vpcli.domain.myuml.myelement.MyElementFactory;
import plugins.vpcli.domain.myuml.myelement.MyElementPool;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;

import java.util.List;

@Singleton
public class MyClassFactory extends MyElementFactory {

    @Inject
    public MyClassFactory(MyElementPool myElementPool) {
        super(myElementPool);
    }

    @Override
    public MyElement reallyCreate(MyElement parent, IModelElement vpElement, List<MyStereotype> stereotypes) {
        return new MyClass(
            parent,
            vpElement.getId(),
            vpElement.getName(),
            vpElement.getDescription(),
            stereotypes);
    }
}
