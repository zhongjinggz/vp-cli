package plugins.vpcli.domain.myuml.mymodel;

import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.myelement.MyElement;
import plugins.vpcli.domain.myuml.myelement.MyElementFactory;
import plugins.vpcli.domain.myuml.myelement.MyElementPool;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;

import java.util.List;

@Singleton
public class MyModelFactory extends MyElementFactory {

    @Inject
    public MyModelFactory(MyElementPool myElementPool ) {
        super(myElementPool);
    }

    @Override
    public MyElement reallyCreate(MyElement parent, IModelElement vpElement, List<MyStereotype> myStereotypes) {
        return new MyModel(
            parent,
            vpElement.getId(),
            vpElement.getName(),
            vpElement.getDescription(),
            myStereotypes
        );
    }
}