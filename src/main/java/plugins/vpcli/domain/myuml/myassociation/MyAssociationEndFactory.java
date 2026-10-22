package plugins.vpcli.domain.myuml.myassociation;

import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElementPool;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.myuml.mycommon.MyStereotype;

import java.util.List;

@Singleton
public class MyAssociationEndFactory extends MyElementFactory {
    @Inject
    public MyAssociationEndFactory(MyElementPool myElementPool) {
        super(myElementPool);
    }

    @Override
    public MyAssociationEnd reallyCreate(
        MyElement parent,
        IModelElement vpAssociationEnd,
        List<MyStereotype> stereotypes
    ) {
        IAssociationEnd vpEnd = (IAssociationEnd) vpAssociationEnd;

        MyElement endpointElement = buildRelatedElement(vpEnd.getModelElement());

        return new MyAssociationEnd(
            parent,
            vpAssociationEnd.getId(),
            vpAssociationEnd.getName(),
            vpAssociationEnd.getDescription(),
            stereotypes,
            endpointElement,
            vpEnd.getMultiplicity(),
            vpEnd.getNavigable()
        );
    }
}
