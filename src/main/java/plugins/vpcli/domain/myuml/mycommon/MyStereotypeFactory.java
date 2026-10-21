package plugins.vpcli.domain.myuml.mycommon;

import com.vp.plugin.model.IModelElement;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public class MyStereotypeFactory extends MyElementFactory {

    @Inject
    public MyStereotypeFactory(MyElementPool myElementPool ) {
        super(myElementPool);
    }

    @Override
    public MyStereotype reallyCreate(MyElement parent, IModelElement vpElement, List<MyStereotype> myStereotypes) {
        return new MyStereotype(
            parent,
            vpElement.getId(),
            vpElement.getName(),
            vpElement.getDescription()
        );
    }
}