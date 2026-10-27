package plugins.vpcli.domain.myuml.mymodel;

import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElement_old2;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElementFactory;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElementPool;

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
    public MyElement_old2 reallyCreate(MyElement_old2 parent, IModelElement vpElement, List<MyStereotype> myStereotypes) {
        return new MyModel(
            parent,
            vpElement.getId(),
            vpElement.getName(),
            vpElement.getDescription(),
            myStereotypes
        );
    }
}