package plugins.vpcli.domain.myuml.myassociation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.myuml.myelement.MyElementFactoryFactory;
import plugins.vpcli.domain.myuml.myelement.MyElementPool;
import plugins.vpcli.domain.myuml.myelement.MyElementType;
import plugins.vpcli.domain.myuml.mydependency.MyDependencyFactory;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;
import plugins.vpcli.domain.myuml.myclassifier.MyClassFactory;
import plugins.vpcli.domain.myuml.mymodel.MyModelFactory;
import plugins.vpcli.domain.myuml.mypackage.MyPackageFactory;
import plugins.vpcli.domain.myuml.mymodelrelationshipcontainer.MyModelRelationshipContainerFactory;
import plugins.vpcli.domain.myuml.myproject.MyProject;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotypeFactory;
import plugins.vpcli.domain.myuml.myunknownelement.UnimplementedElementFactory;
import plugins.vpcli.domain.myuml.myusecase.MyUseCaseFactory;

import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.factory.IModelElementFactory;

class MyAssociationEndFactoryTest {

    private static final List<MyStereotype> stereotypes = new ArrayList<>();

    private final MyProject root = new MyProject("p", "项目");

    private MyElementFactoryFactory buildFactoryFactory(MyElementPool pool) {
        return new MyElementFactoryFactory(
            new MyPackageFactory(pool),
            new MyClassFactory(pool),
            new MyModelFactory(pool),
            new MyUseCaseFactory(pool),
            new MyAssociationFactory(pool),
            new MyAssociationEndFactory(pool),
            new MyDependencyFactory(pool),
            new MyModelRelationshipContainerFactory(pool),
            new MyStereotypeFactory(pool),
            new UnimplementedElementFactory(pool)
        );
    }

    @Test
    void shouldCreateAssociationEndWithMultiplicity() {
        MyElementPool pool = mock(MyElementPool.class);
        when(pool.getMyProject()).thenReturn(root);

        IModelElement vpEndpoint = mock(IModelElement.class);
        when(vpEndpoint.getModelType()).thenReturn(IModelElementFactory.MODEL_TYPE_CLASS);
        when(vpEndpoint.getId()).thenReturn("c1");
        when(vpEndpoint.getName()).thenReturn("订单");
        when(vpEndpoint.getDescription()).thenReturn("");

        IAssociationEnd vpEnd = mock(IAssociationEnd.class);
        when(vpEnd.getModelElement()).thenReturn(vpEndpoint);
        when(vpEnd.getMultiplicity()).thenReturn("0..*");
        when(vpEnd.getNavigable()).thenReturn(MyAssociationEnd.NAVIGABLE_NAVIGABLE);
        when(vpEnd.getAggregationKind()).thenReturn(MyAssociationEnd.AGGREGATION_SHARED);

        MyAssociationEndFactory factory = new MyAssociationEndFactory(pool);
        factory.setFactoryFactory(buildFactoryFactory(pool));

        MyAssociationEnd end = factory.reallyCreate(root, vpEnd, stereotypes);

        assertEquals(MyElementType.ASSOCIATION_END, end.getElementType());
        assertEquals("0..*", end.getMultiplicity());
        assertEquals(MyAssociationEnd.NAVIGABLE_NAVIGABLE, end.getNavigable());
        assertEquals(MyAssociationEnd.AGGREGATION_SHARED, end.getAggregationKind());
        assertEquals("订单", end.getElement().getName());
        assertEquals(MyElementType.CLASS, end.getElement().getElementType());
    }

    @Test
    void shouldNormalizeUnspecifiedMultiplicity() {
        MyElementPool pool = mock(MyElementPool.class);
        when(pool.getMyProject()).thenReturn(root);

        IModelElement vpEndpoint = mock(IModelElement.class);
        when(vpEndpoint.getModelType()).thenReturn(IModelElementFactory.MODEL_TYPE_CLASS);
        when(vpEndpoint.getId()).thenReturn("c1");
        when(vpEndpoint.getName()).thenReturn("订单");
        when(vpEndpoint.getDescription()).thenReturn("");

        IAssociationEnd vpEnd = mock(IAssociationEnd.class);
        when(vpEnd.getModelElement()).thenReturn(vpEndpoint);
        when(vpEnd.getMultiplicity()).thenReturn("Unspecified");
        when(vpEnd.getNavigable()).thenReturn(MyAssociationEnd.NAVIGABLE_UNSPECIFIED);
        when(vpEnd.getAggregationKind()).thenReturn(MyAssociationEnd.AGGREGATION_NONE);

        MyAssociationEndFactory factory = new MyAssociationEndFactory(pool);
        factory.setFactoryFactory(buildFactoryFactory(pool));

        MyAssociationEnd end = factory.reallyCreate(root, vpEnd, stereotypes);

        assertEquals("", end.getMultiplicity());
        assertEquals(MyAssociationEnd.NAVIGABLE_UNSPECIFIED, end.getNavigable());
    }
}
