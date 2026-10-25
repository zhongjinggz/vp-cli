package plugins.vpcli.domain.myuml.mydependency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.myuml.myassociation.MyAssociationEndFactory;
import plugins.vpcli.domain.myuml.myassociation.MyAssociationFactory;
import plugins.vpcli.domain.myuml.myclassifier.MyClass;
import plugins.vpcli.domain.myuml.myclassifier.MyClassFactory;
import plugins.vpcli.domain.myuml.myelement.MyElementFactoryFactory;
import plugins.vpcli.domain.myuml.myelement.MyElementPool;
import plugins.vpcli.domain.myuml.myelement.MyElementType;
import plugins.vpcli.domain.myuml.mygeneralization.MyGeneralizationFactory;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;
import plugins.vpcli.domain.myuml.mymodel.MyModelFactory;
import plugins.vpcli.domain.myuml.mypackage.MyPackageFactory;
import plugins.vpcli.domain.myuml.mymodelrelationshipcontainer.MyModelRelationshipContainerFactory;
import plugins.vpcli.domain.myuml.myproject.MyProject;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotypeFactory;
import plugins.vpcli.domain.myuml.myunknownelement.UnimplementedElementFactory;
import plugins.vpcli.domain.myuml.myusecase.MyUseCaseFactory;

import com.vp.plugin.model.IDependency;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.factory.IModelElementFactory;

class MyDependencyTest {

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
            new MyGeneralizationFactory(pool),
            new MyModelRelationshipContainerFactory(pool),
            new MyStereotypeFactory(pool),
            new UnimplementedElementFactory(pool)
        );
    }

    @Test
    void shouldExposeEndpoints() {
        MyClass from = new MyClass(root, "c1", "订单", "", stereotypes);
        MyClass to = new MyClass(root, "c2", "客户", "", stereotypes);

        MyDependency dependency = new MyDependency(root, "d1", "使用", "", stereotypes, from, to);

        assertSame(from, dependency.getFrom());
        assertSame(to, dependency.getTo());
        assertEquals(MyElementType.DEPENDENCY, dependency.getElementType());
        assertEquals("使用", dependency.getName());
    }

    @Test
    void shouldDefaultEndpointsToNull() {
        MyDependency dependency = new MyDependency(root, "d1", "", "", stereotypes, null, null);
        assertNull(dependency.getFrom());
        assertNull(dependency.getTo());
    }

    @Test
    void factoryShouldPopulateEndpointsFromVpRelationship() {
        IModelElement vpFrom = mock(IModelElement.class);
        when(vpFrom.getModelType()).thenReturn(IModelElementFactory.MODEL_TYPE_CLASS);
        when(vpFrom.getId()).thenReturn("c1");
        when(vpFrom.getName()).thenReturn("订单");
        when(vpFrom.getDescription()).thenReturn("");

        IModelElement vpTo = mock(IModelElement.class);
        when(vpTo.getModelType()).thenReturn(IModelElementFactory.MODEL_TYPE_CLASS);
        when(vpTo.getId()).thenReturn("c2");
        when(vpTo.getName()).thenReturn("客户");
        when(vpTo.getDescription()).thenReturn("");

        IDependency dependencyVp = mock(IDependency.class);
        when(dependencyVp.getId()).thenReturn("d1");
        when(dependencyVp.getName()).thenReturn("使用");
        when(dependencyVp.getDescription()).thenReturn("");
        when(dependencyVp.getFrom()).thenReturn(vpFrom);
        when(dependencyVp.getTo()).thenReturn(vpTo);

        MyElementPool pool = mock(MyElementPool.class);
        when(pool.getMyProject()).thenReturn(root);

        MyDependencyFactory factory = new MyDependencyFactory(pool);
        factory.setFactoryFactory(buildFactoryFactory(pool));

        MyDependency dependency = factory.reallyBuild(root, dependencyVp, stereotypes);

        assertEquals(MyElementType.DEPENDENCY, dependency.getElementType());
        assertEquals("使用", dependency.getName());
        assertEquals("订单", dependency.getFrom().getName());
        assertEquals(MyElementType.CLASS, dependency.getFrom().getElementType());
        assertEquals("客户", dependency.getTo().getName());
        assertEquals(MyElementType.CLASS, dependency.getTo().getElementType());
    }
}
