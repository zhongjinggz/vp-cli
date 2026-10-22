package plugins.vpcli.domain.myuml.myassociation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.myuml.myclassifier.MyClass;
import plugins.vpcli.domain.myuml.myclassifier.MyClassFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactoryFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElementPool;
import plugins.vpcli.domain.myuml.mycommon.MyElementType;
import plugins.vpcli.domain.myuml.mycommon.MyStereotype;
import plugins.vpcli.domain.myuml.mymodel.MyModelFactory;
import plugins.vpcli.domain.myuml.mypackage.MyPackageFactory;
import plugins.vpcli.domain.myuml.mymodelrelationshipcontainer.MyModelRelationshipContainerFactory;
import plugins.vpcli.domain.myuml.myproject.MyProject;
import plugins.vpcli.domain.myuml.myusecase.MyUseCase;
import plugins.vpcli.domain.myuml.myusecase.MyUseCaseFactory;

import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.factory.IModelElementFactory;

class MyAssociationTest {

    private static final List<MyStereotype> stereotypes = new ArrayList<>();

    private final MyProject root = new MyProject("p", "项目");

    @Test
    void shouldExposeEndpoints() {
        MyClass from = new MyClass(root, "c1", "订单", "", stereotypes);
        MyUseCase to = new MyUseCase(root, "u1", "下订单", "", stereotypes);

        MyAssociation association = new MyAssociation(
            root, "a1", "", "",
            stereotypes, from, to);

        assertSame(from, association.getFromElement());
        assertSame(to, association.getToElement());
        assertEquals(MyElementType.ASSOCIATION, association.getElementType());
    }

    @Test
    void shouldDefaultEndpointsToNull() {
        MyAssociation association = new MyAssociation(root, "a1", "", "", stereotypes);
        assertNull(association.getFromElement());
        assertNull(association.getToElement());
    }

    @Test
    void factoryShouldPopulateEndpointsFromVpRelationship() {
        // 端点：一个 Class 目标模型 + 一个 UseCase 目标模型
        IModelElement vpFrom = mock(IModelElement.class);
        when(vpFrom.getModelType()).thenReturn(IModelElementFactory.MODEL_TYPE_CLASS);
        when(vpFrom.getId()).thenReturn("c1");
        when(vpFrom.getName()).thenReturn("订单");
        when(vpFrom.getDescription()).thenReturn("");

        IModelElement vpTo = mock(IModelElement.class);
        when(vpTo.getModelType()).thenReturn(IModelElementFactory.MODEL_TYPE_USE_CASE);
        when(vpTo.getId()).thenReturn("u1");
        when(vpTo.getName()).thenReturn("下订单");
        when(vpTo.getDescription()).thenReturn("");

        IAssociation associationVp = mock(IAssociation.class);
        when(associationVp.getId()).thenReturn("a1");
        when(associationVp.getName()).thenReturn("");
        when(associationVp.getDescription()).thenReturn("");
        when(associationVp.getFrom()).thenReturn(vpFrom);
        when(associationVp.getTo()).thenReturn(vpTo);

        MyElementPool pool = mock(MyElementPool.class);
        when(pool.getMyProject()).thenReturn(root);

        MyElementFactoryFactory factoryFactory = new MyElementFactoryFactory(
            new MyPackageFactory(pool),
            new MyClassFactory(pool),
            new MyModelFactory(pool),
            new MyUseCaseFactory(pool),
            new MyAssociationFactory(pool),
            new MyModelRelationshipContainerFactory(pool),
            new plugins.vpcli.domain.myuml.mycommon.MyStereotypeFactory(pool)
        );

        MyAssociationFactory factory = new MyAssociationFactory(pool);
        factory.setFactoryFactory(factoryFactory);

        MyAssociation association = factory.reallyCreate(root, associationVp, stereotypes);

        assertEquals(MyElementType.ASSOCIATION, association.getElementType());
        assertEquals("订单", association.getFromElement().getName());
        assertEquals(MyElementType.CLASS, association.getFromElement().getElementType());
        assertEquals("下订单", association.getToElement().getName());
        assertEquals(MyElementType.USE_CASE, association.getToElement().getElementType());
    }

}
