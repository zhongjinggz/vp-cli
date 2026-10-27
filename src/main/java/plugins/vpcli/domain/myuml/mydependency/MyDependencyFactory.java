package plugins.vpcli.domain.myuml.mydependency;

import com.vp.plugin.model.IDependency;
import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElement_old2;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElementFactory;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElementPool;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public class MyDependencyFactory extends MyElementFactory {
    @Inject
    public MyDependencyFactory(MyElementPool myElementPool) {
        super(myElementPool);
    }

    @Override
    public MyDependency reallyCreate(
        MyElement_old2 parent,
        IModelElement vpElement,
        List<MyStereotype> stereotypes
    ) {
        IDependency vpDependency = (IDependency) vpElement;

        // 依赖在 VP 中没有独立的 relationshipEnd 模型，端点直接从关系两端取（IRelationship.getFrom/getTo）
        MyElement_old2 from = buildRelatedElement(vpDependency.getFrom());
        MyElement_old2 to = buildRelatedElement(vpDependency.getTo());

        return new MyDependency(
            parent,
            vpElement.getId(),
            vpElement.getName(),
            vpElement.getDescription(),
            stereotypes,
            from,
            to
        );
    }
}
