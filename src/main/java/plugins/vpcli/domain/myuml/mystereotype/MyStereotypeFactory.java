package plugins.vpcli.domain.myuml.mystereotype;

import com.vp.plugin.model.IModelElement;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.myuml.myelement.MyElement;
import plugins.vpcli.domain.myuml.myelement.MyElementFactory;
import plugins.vpcli.domain.myuml.myelement.MyElementPool;

import java.util.List;

@Singleton
public class MyStereotypeFactory extends MyElementFactory {

    @Inject
    public MyStereotypeFactory(MyElementPool myElementPool ) {
        super(myElementPool);
    }

    @Override
    public MyStereotype reallyBuild(MyElement parent, IModelElement vpElement, List<MyStereotype> myStereotypes) {
        return new MyStereotype(
            parent,
            vpElement.getId(),
            vpElement.getName(),
            vpElement.getDescription()
        );
    }
}