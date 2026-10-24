package plugins.vpcli.domain.myuml.mygeneralization;

import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.myelement.MyElement;
import plugins.vpcli.domain.myuml.myelement.MyElementFactory;
import plugins.vpcli.domain.myuml.myelement.MyElementPool;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public class MyGeneralizationFactory extends MyElementFactory {
    @Inject
    public MyGeneralizationFactory(MyElementPool myElementPool) {
        super(myElementPool);
    }

    @Override
    public MyGeneralization reallyCreate(
        MyElement parent,
        IModelElement vpElement,
        List<MyStereotype> stereotypes
    ) {
        IGeneralization vpGeneralization = (IGeneralization) vpElement;

        // IGeneralization 无专属方向方法，经继承链 ISimpleRelationship→IRelationship 暴露 getFrom()/getTo()
        // VP 语义：getFrom()=父类(general)、getTo()=子类(specific)。方向恒定，无需交换。
        MyElement general = buildRelatedElement(vpGeneralization.getFrom());
        MyElement specific = buildRelatedElement(vpGeneralization.getTo());

        return new MyGeneralization(
            parent,
            vpElement.getId(),
            vpElement.getName(),
            vpElement.getDescription(),
            stereotypes,
            general,
            specific
        );
    }
}
