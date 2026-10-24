package plugins.vpcli.domain.myuml.mygeneralization;

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
import plugins.vpcli.domain.myuml.mydependency.MyDependencyFactory;
import plugins.vpcli.domain.myuml.mymodelrelationshipcontainer.MyModelRelationshipContainerFactory;
import plugins.vpcli.domain.myuml.mymodel.MyModelFactory;
import plugins.vpcli.domain.myuml.mypackage.MyPackageFactory;
import plugins.vpcli.domain.myuml.myproject.MyProject;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotypeFactory;
import plugins.vpcli.domain.myuml.myunknownelement.UnimplementedElementFactory;
import plugins.vpcli.domain.myuml.myusecase.MyUseCaseFactory;

import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.factory.IModelElementFactory;

class MyGeneralizationTest {

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
    void shouldExposeGeneralAndSpecific() {
        MyClass general = new MyClass(root, "c1", "动物", "", stereotypes);
        MyClass specific = new MyClass(root, "c2", "狗", "", stereotypes);

        MyGeneralization generalization =
            new MyGeneralization(root, "g1", "继承", "", stereotypes, general, specific);

        assertSame(general, generalization.getGeneral());
        assertSame(specific, generalization.getSpecific());
        assertEquals(MyElementType.GENERALIZATION, generalization.getElementType());
        assertEquals("继承", generalization.getName());
    }

    @Test
    void shouldDefaultEndpointsToNull() {
        MyGeneralization generalization =
            new MyGeneralization(root, "g1", "", "", stereotypes, null, null);
        assertNull(generalization.getGeneral());
        assertNull(generalization.getSpecific());
    }

    @Test
    void factoryShouldPopulateGeneralSpecificFromVpRelationship() {
        // 语义约定：VP getFrom()=general(父)、getTo()=specific(子)
        IModelElement vpGeneral = mock(IModelElement.class);
        when(vpGeneral.getModelType()).thenReturn(IModelElementFactory.MODEL_TYPE_CLASS);
        when(vpGeneral.getId()).thenReturn("c1");
        when(vpGeneral.getName()).thenReturn("动物");
        when(vpGeneral.getDescription()).thenReturn("");

        IModelElement vpSpecific = mock(IModelElement.class);
        when(vpSpecific.getModelType()).thenReturn(IModelElementFactory.MODEL_TYPE_CLASS);
        when(vpSpecific.getId()).thenReturn("c2");
        when(vpSpecific.getName()).thenReturn("狗");
        when(vpSpecific.getDescription()).thenReturn("");

        IGeneralization generalizationVp = mock(IGeneralization.class);
        when(generalizationVp.getId()).thenReturn("g1");
        when(generalizationVp.getName()).thenReturn("继承");
        when(generalizationVp.getDescription()).thenReturn("");
        when(generalizationVp.getFrom()).thenReturn(vpGeneral);
        when(generalizationVp.getTo()).thenReturn(vpSpecific);

        MyElementPool pool = mock(MyElementPool.class);
        when(pool.getMyProject()).thenReturn(root);

        MyGeneralizationFactory factory = new MyGeneralizationFactory(pool);
        factory.setFactoryFactory(buildFactoryFactory(pool));

        MyGeneralization generalization = factory.reallyCreate(root, generalizationVp, stereotypes);

        assertEquals(MyElementType.GENERALIZATION, generalization.getElementType());
        assertEquals("继承", generalization.getName());
        assertEquals("动物", generalization.getGeneral().getName());
        assertEquals(MyElementType.CLASS, generalization.getGeneral().getElementType());
        assertEquals("狗", generalization.getSpecific().getName());
        assertEquals(MyElementType.CLASS, generalization.getSpecific().getElementType());
    }
}
