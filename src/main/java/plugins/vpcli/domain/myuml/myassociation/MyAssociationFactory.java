package plugins.vpcli.domain.myuml.myassociation;

import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElementPool;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.myuml.mycommon.MyStereotype;

import java.util.List;

@Singleton
public class MyAssociationFactory extends MyElementFactory {
    @Inject
    public MyAssociationFactory(MyElementPool myElementPool) {
        super(myElementPool);
    }

    @Override
    public MyAssociation reallyCreate(
        MyElement parent,
        IModelElement vpAssociation,
        List<MyStereotype> stereotypes
    ) {
        var fromElement = buildRelatedElement(((IAssociation)vpAssociation).getFrom());
        var toElement = buildRelatedElement(((IAssociation)vpAssociation).getTo());

        MyAssociation result = new MyAssociation(
            parent,
            vpAssociation.getId(),
            vpAssociation.getName(),
            vpAssociation.getDescription(),
            stereotypes,
            fromElement,
            toElement
        );

        return result;
    }
}
