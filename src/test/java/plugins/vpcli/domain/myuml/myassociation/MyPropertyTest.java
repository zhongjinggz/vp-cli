package plugins.vpcli.domain.myuml.myassociation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.myuml.myclassifier.MyClass;
import plugins.vpcli.domain.myuml.myelement.MyElementType;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;
import plugins.vpcli.domain.myuml.myproject.MyProject;

class MyPropertyTest {

    private static final List<MyStereotype> stereotypes = new ArrayList<>();

    @Test
    void shouldExposeElementAndMultiplicity() {
        MyProject root = new MyProject("p", "项目");
        MyClass order = new MyClass(root, "c1", "订单", "", stereotypes);

        MyProperty end = new MyProperty(
            root, "e1", "订单端", "", stereotypes, order, "0..*",
            MyProperty.NAVIGABLE_NAVIGABLE, MyProperty.AGGREGATION_NONE);

        assertEquals(MyElementType.PROPERTY, end.getElementType());
        assertSame(order, end.getElement());
        assertEquals("0..*", end.getMultiplicity());
        assertEquals(MyProperty.NAVIGABLE_NAVIGABLE, end.getNavigable());
    }

    @Test
    void shouldNormalizeUnspecifiedMultiplicityToEmpty() {
        MyProject root = new MyProject("p", "项目");
        MyClass order = new MyClass(root, "c1", "订单", "", stereotypes);

        MyProperty end = new MyProperty(
            root, "e1", "订单端", "", stereotypes, order, "Unspecified",
            MyProperty.NAVIGABLE_NAVIGABLE, MyProperty.AGGREGATION_NONE);

        assertEquals("", end.getMultiplicity());
    }

    @Test
    void shouldNormalizeNullMultiplicityToEmpty() {
        MyProject root = new MyProject("p", "项目");
        MyClass order = new MyClass(root, "c1", "订单", "", stereotypes);

        MyProperty end = new MyProperty(
            root, "e1", "订单端", "", stereotypes, order, null,
            MyProperty.NAVIGABLE_NAVIGABLE, MyProperty.AGGREGATION_NONE);

        assertEquals("", end.getMultiplicity());
    }

    @Test
    void shouldBuildQualifiedNameAlongParentChain() {
        MyProject root = new MyProject("p", "项目");
        MyClass order = new MyClass(root, "c1", "订单", "", stereotypes);

        MyProperty end = new MyProperty(
            root, "e1", "订单端", "", stereotypes, order, "",
            MyProperty.NAVIGABLE_NON_NAVIGABLE, MyProperty.AGGREGATION_NONE);

        assertEquals("订单端", end.getQualifiedName());
    }

    @Test
    void shouldDefaultNavigableToUnspecified() {
        MyProject root = new MyProject("p", "项目");
        MyClass order = new MyClass(root, "c1", "订单", "", stereotypes);

        MyProperty end = new MyProperty(
            root, "e1", "订单端", "", stereotypes, order, "",
            MyProperty.NAVIGABLE_UNSPECIFIED, MyProperty.AGGREGATION_NONE);

        assertEquals(MyProperty.NAVIGABLE_UNSPECIFIED, end.getNavigable());
    }
}
