package plugins.vpcli.domain.myuml.myunknownelement;

import com.vp.plugin.model.IModelElement;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElement_old2;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElementFactory;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElementPool;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;

import java.util.List;

@Singleton
public class UnimplementedElementFactory extends MyElementFactory {

    @Inject
    public UnimplementedElementFactory(MyElementPool myElementPool) {
        super(myElementPool);
    }

    @Override
    public UnimplementedElement reallyCreate(
        MyElement_old2 parent,
        IModelElement vpElement,
        List<MyStereotype> stereotypes
    ) {
        return new UnimplementedElement(
            parent,
            vpElement.getId(),
            vpElement.getName(),
            vpElement.getDescription(),
            stereotypes,
            vpElement.getModelType()
        );
    }
}
