package plugins.vpcli.domain.myuml.myassociation;

import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.myelement.MyElement;
import plugins.vpcli.domain.myuml.myelement.MyElementFactory;
import plugins.vpcli.domain.myuml.myelement.MyElementPool;

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
    public MyProperty reallyBuild(
        MyElement parent,
        IModelElement vpElement,
        List<MyStereotype> stereotypes
    ) {
        IAssociationEnd vpAssociationEnd = (IAssociationEnd) vpElement;

        MyElement endpointElement = buildRelatedElement(vpAssociationEnd.getModelElement());

        return new MyProperty(
            parent,
            vpElement.getId(),
            vpElement.getName(),
            vpElement.getDescription(),
            stereotypes,
            endpointElement,
            vpAssociationEnd.getMultiplicity(),
            vpAssociationEnd.getNavigable(),
            vpAssociationEnd.getAggregationKind()
        );
    }
}
