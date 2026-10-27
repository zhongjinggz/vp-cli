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
import plugins.vpcli.domain.myuml_old.myelement_old.MyElementFactoryFactory;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElementPool;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElementType;
import plugins.vpcli.domain.myuml.mydependency.MyDependencyFactory;
import plugins.vpcli.domain.myuml.mygeneralization.MyGeneralizationFactory;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;
import plugins.vpcli.domain.myuml.mymodel.MyModelFactory;
import plugins.vpcli.domain.myuml_old.mypackage_old.MyPackageFactory;
import plugins.vpcli.domain.myuml.mymodelrelationshipcontainer.MyModelRelationshipContainerFactory;
import plugins.vpcli.domain.myuml.myproject.MyProject;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotypeFactory;
import plugins.vpcli.domain.myuml.myunknownelement.UnimplementedElementFactory;
import plugins.vpcli.domain.myuml_old.myusecase_old.MyUseCase_old;
import plugins.vpcli.domain.myuml_old.myusecase_old.MyUseCaseFactory;

import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.factory.IModelElementFactory;

class MyAssociationTest {

    private static final List<MyStereotype> stereotypes = new ArrayList<>();

    private final MyProject root = new MyProject("p", "项目");

    @Test
    void shouldExposeEndpoints() {
        MyClass from = new MyClass(root, "c1", "订单", "", stereotypes);
        MyUseCase_old to = new MyUseCase_old(root, "u1", "下订单", "", stereotypes);

        MyAssociationEnd fromEnd = new MyAssociationEnd(root, "fe", "", "", stereotypes, from, "1",
            MyAssociationEnd.NAVIGABLE_NAVIGABLE, MyAssociationEnd.AGGREGATION_NONE);
        MyAssociationEnd toEnd = new MyAssociationEnd(root, "te", "", "", stereotypes, to, "0..*",
            MyAssociationEnd.NAVIGABLE_NON_NAVIGABLE, MyAssociationEnd.AGGREGATION_NONE);

        MyAssociation association = new MyAssociation(
            root, "a1", "", "",
            stereotypes, fromEnd, toEnd);

        assertSame(fromEnd, association.getFromEnd());
        assertSame(toEnd, association.getToEnd());
        assertSame(from, association.getFromElement());
        assertSame(to, association.getToElement());
        assertEquals("1", association.getFromEnd().getMultiplicity());
        assertEquals("0..*", association.getToEnd().getMultiplicity());
        assertEquals(MyElementType.ASSOCIATION, association.getElementType());
    }

    @Test
    void shouldDefaultEndpointsToNull() {
        MyAssociation association = new MyAssociation(root, "a1", "", "", stereotypes, null, null);
        assertNull(association.getFromElement());
        assertNull(association.getToElement());
        assertNull(association.getFromEnd());
        assertNull(association.getToEnd());
    }

    @Test
    void shouldPopulateEndpointsThroughCompatibilitySetters() {
        MyClass from = new MyClass(root, "c1", "订单", "", stereotypes);
        MyUseCase_old to = new MyUseCase_old(root, "u1", "下订单", "", stereotypes);

        MyAssociation association = new MyAssociation(root, "a1", "", "", stereotypes, null, null);
        association.setFromElement(from);
        association.setToElement(to);

        assertSame(from, association.getFromElement());
        assertSame(to, association.getToElement());
        assertEquals(MyElementType.ASSOCIATION_END, association.getFromEnd().getElementType());
        assertEquals(MyElementType.ASSOCIATION_END, association.getToEnd().getElementType());
    }

    @Test
    void factoryShouldPopulateEndpointsFromVpRelationship() {        // 端点：一个 Class 目标模型 + 一个 UseCase 目标模型
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

        IAssociationEnd vpFromEnd = mock(IAssociationEnd.class);
        when(vpFromEnd.getModelType()).thenReturn(IModelElementFactory.MODEL_TYPE_ASSOCIATION_END);
        when(vpFromEnd.getId()).thenReturn("fe1");
        when(vpFromEnd.getName()).thenReturn("订单端");
        when(vpFromEnd.getDescription()).thenReturn("");
        when(vpFromEnd.getModelElement()).thenReturn(vpFrom);
        when(vpFromEnd.getMultiplicity()).thenReturn("1");
        when(vpFromEnd.getNavigable()).thenReturn(MyAssociationEnd.NAVIGABLE_NAVIGABLE);

        IAssociationEnd vpToEnd = mock(IAssociationEnd.class);
        when(vpToEnd.getModelType()).thenReturn(IModelElementFactory.MODEL_TYPE_ASSOCIATION_END);
        when(vpToEnd.getId()).thenReturn("te1");
        when(vpToEnd.getName()).thenReturn("客户端");
        when(vpToEnd.getDescription()).thenReturn("");
        when(vpToEnd.getModelElement()).thenReturn(vpTo);
        when(vpToEnd.getMultiplicity()).thenReturn("0..*");
        when(vpToEnd.getNavigable()).thenReturn(MyAssociationEnd.NAVIGABLE_NON_NAVIGABLE);

        IAssociation associationVp = mock(IAssociation.class);
        when(associationVp.getId()).thenReturn("a1");
        when(associationVp.getName()).thenReturn("");
        when(associationVp.getDescription()).thenReturn("");
        when(associationVp.getFromEnd()).thenReturn(vpFromEnd);
        when(associationVp.getToEnd()).thenReturn(vpToEnd);

        MyElementPool pool = mock(MyElementPool.class);
        when(pool.getMyProject()).thenReturn(root);

        MyElementFactoryFactory factoryFactory = new MyElementFactoryFactory(
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

        MyAssociationFactory factory = new MyAssociationFactory(pool);
        factory.setFactoryFactory(factoryFactory);

        MyAssociation association = factory.reallyCreate(root, associationVp, stereotypes);

        assertEquals(MyElementType.ASSOCIATION, association.getElementType());
        assertEquals("订单", association.getFromElement().getName());
        assertEquals(MyElementType.CLASS, association.getFromElement().getElementType());
        assertEquals("1", association.getFromEnd().getMultiplicity());
        assertEquals(MyElementType.ASSOCIATION_END, association.getFromEnd().getElementType());
        assertEquals(MyAssociationEnd.NAVIGABLE_NAVIGABLE, association.getFromEnd().getNavigable());
        assertEquals("下订单", association.getToElement().getName());
        assertEquals(MyElementType.USE_CASE, association.getToElement().getElementType());
        assertEquals("0..*", association.getToEnd().getMultiplicity());
        assertEquals(MyAssociationEnd.NAVIGABLE_NON_NAVIGABLE, association.getToEnd().getNavigable());
    }

    // ---- 关联名称阅读方向（#9） ----

    @Test
    void shouldExposeNameDirectionEnumValues() {
        // 方案步骤 1 的验证：三个取值存在，且 NONE 是默认
        MyAssociation association = new MyAssociation(root, "a1", "", "", stereotypes, null, null);
        assertEquals(MyAssociation.NameDirection.NONE, association.getNameDirection(),
            "默认应为 NONE");
    }

    @Test
    void shouldSetNameDirectionIgnoringNull() {
        MyAssociation association = new MyAssociation(root, "a1", "", "", stereotypes, null, null);
        association.setNameDirection(MyAssociation.NameDirection.FROM_TO);
        assertEquals(MyAssociation.NameDirection.FROM_TO, association.getNameDirection());

        // 传 null → 回退 NONE（腐化脏数据保护）
        association.setNameDirection(null);
        assertEquals(MyAssociation.NameDirection.NONE, association.getNameDirection());
    }

    @Test
    void factoryShouldCaptureFromToDirection() {
        IAssociation associationVp = associationVpWithDirection(IAssociation.DIRECTION_FROM_TO);
        MyAssociation association = buildViaFactory(associationVp);
        assertEquals(MyAssociation.NameDirection.FROM_TO, association.getNameDirection());
    }

    @Test
    void factoryShouldCaptureToFromDirection() {
        IAssociation associationVp = associationVpWithDirection(IAssociation.DIRECTION_TO_FROM);
        MyAssociation association = buildViaFactory(associationVp);
        assertEquals(MyAssociation.NameDirection.TO_FROM, association.getNameDirection());
    }

    @Test
    void factoryShouldDefaultNameDirectionToNoneWhenUnknown() {
        IAssociation associationVp = associationVpWithDirection(-999);
        MyAssociation association = buildViaFactory(associationVp);
        assertEquals(MyAssociation.NameDirection.NONE, association.getNameDirection());
    }

    private IAssociation associationVpWithDirection(int direction) {
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

        IAssociationEnd vpFromEnd = mock(IAssociationEnd.class);
        when(vpFromEnd.getModelType()).thenReturn(IModelElementFactory.MODEL_TYPE_ASSOCIATION_END);
        when(vpFromEnd.getId()).thenReturn("fe1");
        when(vpFromEnd.getName()).thenReturn("");
        when(vpFromEnd.getDescription()).thenReturn("");
        when(vpFromEnd.getModelElement()).thenReturn(vpFrom);
        when(vpFromEnd.getMultiplicity()).thenReturn("1");
        when(vpFromEnd.getNavigable()).thenReturn(MyAssociationEnd.NAVIGABLE_UNSPECIFIED);

        IAssociationEnd vpToEnd = mock(IAssociationEnd.class);
        when(vpToEnd.getModelType()).thenReturn(IModelElementFactory.MODEL_TYPE_ASSOCIATION_END);
        when(vpToEnd.getId()).thenReturn("te1");
        when(vpToEnd.getName()).thenReturn("");
        when(vpToEnd.getDescription()).thenReturn("");
        when(vpToEnd.getModelElement()).thenReturn(vpTo);
        when(vpToEnd.getMultiplicity()).thenReturn("0..*");
        when(vpToEnd.getNavigable()).thenReturn(MyAssociationEnd.NAVIGABLE_UNSPECIFIED);

        IAssociation associationVp = mock(IAssociation.class);
        when(associationVp.getId()).thenReturn("a1");
        when(associationVp.getName()).thenReturn("");
        when(associationVp.getDescription()).thenReturn("");
        when(associationVp.getFromEnd()).thenReturn(vpFromEnd);
        when(associationVp.getToEnd()).thenReturn(vpToEnd);
        when(associationVp.getDirection()).thenReturn(direction);
        return associationVp;
    }

    private MyAssociation buildViaFactory(IAssociation associationVp) {
        MyElementPool pool = mock(MyElementPool.class);
        when(pool.getMyProject()).thenReturn(root);

        MyElementFactoryFactory factoryFactory = new MyElementFactoryFactory(
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

        MyAssociationFactory factory = new MyAssociationFactory(pool);
        factory.setFactoryFactory(factoryFactory);
        return factory.reallyCreate(root, associationVp, stereotypes);
    }
}
