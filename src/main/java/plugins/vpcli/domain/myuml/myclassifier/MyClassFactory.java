package plugins.vpcli.domain.myuml.myclassifier;

import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElementPool;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.myuml.mycommon.MyStereotype;

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
