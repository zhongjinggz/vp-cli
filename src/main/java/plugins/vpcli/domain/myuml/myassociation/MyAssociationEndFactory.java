package plugins.vpcli.domain.myuml.myassociation;

import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElement_old2;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElementFactory;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElementPool;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;

import java.util.List;

@Singleton
public class MyAssociationEndFactory extends MyElementFactory {
    @Inject
    public MyAssociationEndFactory(MyElementPool myElementPool) {
        super(myElementPool);
    }

    @Override
    public MyAssociationEnd reallyCreate(
        MyElement_old2 parent,
        IModelElement vpAssociationEnd,
        List<MyStereotype> stereotypes
    ) {
        IAssociationEnd vpEnd = (IAssociationEnd) vpAssociationEnd;

        MyElement_old2 endpointElement = buildRelatedElement(vpEnd.getModelElement());

        return new MyAssociationEnd(
            parent,
            vpAssociationEnd.getId(),
            vpAssociationEnd.getName(),
            vpAssociationEnd.getDescription(),
            stereotypes,
            endpointElement,
            vpEnd.getMultiplicity(),
            vpEnd.getNavigable(),
            vpEnd.getAggregationKind()
        );
    }
}
