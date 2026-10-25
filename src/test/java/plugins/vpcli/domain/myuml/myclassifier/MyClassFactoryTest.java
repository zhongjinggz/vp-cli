package plugins.vpcli.domain.myuml.myclassifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.myuml.myelement.MyElementType;
import plugins.vpcli.domain.myuml.myelement.MyElementPool;
import plugins.vpcli.domain.myuml.myproject.MyProject;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;

import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.IParameter;

class MyClassFactoryTest {

    private static final List<MyStereotype> stereotypes = new ArrayList<>();
    private static final MyProject root = new MyProject("p", "项目");

    private static List<IAttribute> attributes() {
        List<IAttribute> attrs = new ArrayList<>();

        IAttribute id = mock(IAttribute.class);
        when(id.getVisibility()).thenReturn("");
        when(id.getName()).thenReturn("id");
        when(id.getTypeAsString()).thenReturn("");
        when(id.getInitialValueAsString()).thenReturn("");
        when(id.getScope()).thenReturn("instance");
        attrs.add(id);

        IAttribute age = mock(IAttribute.class);
        when(age.getVisibility()).thenReturn("public");
        when(age.getName()).thenReturn("age");
        when(age.getTypeAsString()).thenReturn("int");
        when(age.getInitialValueAsString()).thenReturn("18");
        when(age.getScope()).thenReturn("instance");
        attrs.add(age);

        IAttribute status = mock(IAttribute.class);
        when(status.getVisibility()).thenReturn("");
        when(status.getName()).thenReturn("");
        when(status.getTypeAsString()).thenReturn("status");
        when(status.getInitialValueAsString()).thenReturn("");
        when(status.getScope()).thenReturn("instance");
        attrs.add(status);

        return attrs;
    }

    private static List<IOperation> operations() {
        List<IOperation> ops = new ArrayList<>();

        IOperation open = mock(IOperation.class);
        when(open.getVisibility()).thenReturn("");
        when(open.getName()).thenReturn("open");
        when(open.getReturnTypeAsString()).thenReturn("void");
        when(open.isAbstract()).thenReturn(false);
        when(open.getScope()).thenReturn("instance");
        when(open.parameterIterator()).thenReturn(emptyIterator());
        ops.add(open);

        IOperation save = mock(IOperation.class);
        when(save.getVisibility()).thenReturn("private");
        when(save.getName()).thenReturn("save");
        when(save.getReturnTypeAsString()).thenReturn("boolean");
        when(save.isAbstract()).thenReturn(false);
        when(save.getScope()).thenReturn("instance");

        List<IParameter> saveParams = new ArrayList<>();
        IParameter file = mock(IParameter.class);
        when(file.getName()).thenReturn("file");
        when(file.getTypeAsString()).thenReturn("string");
        when(file.getDefaultValueAsString()).thenReturn("");
        saveParams.add(file);
        when(save.parameterIterator()).thenReturn(saveParams.iterator());
        ops.add(save);

        return ops;
    }

    private static Iterator<IAttribute> emptyIterator() {
        return new ArrayList<IAttribute>().iterator();
    }

    @Test
    void factoryShouldPopulateAttributesFromVpClass() {
        IClass vpClass = mock(IClass.class);
        when(vpClass.getId()).thenReturn("c1");
        when(vpClass.getName()).thenReturn("订单");
        when(vpClass.getDescription()).thenReturn("");
        List<IAttribute> attrs = attributes();
        List<IOperation> emptyOps = new ArrayList<>();
        when(vpClass.attributeIterator()).thenReturn(attrs.iterator());
        when(vpClass.operationIterator()).thenReturn(emptyOps.iterator());

        MyElementPool pool = mock(MyElementPool.class);
        when(pool.getMyProject()).thenReturn(root);

        MyClassFactory factory = new MyClassFactory(pool);
        MyClass myClass = (MyClass) factory.reallyBuild(root, vpClass, stereotypes);

        assertEquals(MyElementType.CLASS, myClass.getElementType());
        assertEquals("订单", myClass.getName());
        assertEquals(3, myClass.getAttributes().size());

        MyAttribute first = myClass.getAttributes().get(0);
        assertEquals("", first.getVisibility());
        assertEquals("id", first.getName());
        assertNull(first.getType());

        MyAttribute age = myClass.getAttributes().get(1);
        assertEquals("public", age.getVisibility());
        assertEquals("age", age.getName());
        assertEquals("int", age.getType());
        assertEquals("18", age.getInitialValue());

        MyAttribute status = myClass.getAttributes().get(2);
        assertEquals("", status.getName());
        assertEquals("status", status.getType());
    }

    @Test
    void factoryShouldPopulateOperationsWithParametersFromVpClass() {
        IClass vpClass = mock(IClass.class);
        when(vpClass.getId()).thenReturn("c1");
        when(vpClass.getName()).thenReturn("订单");
        when(vpClass.getDescription()).thenReturn("");
        when(vpClass.attributeIterator()).thenReturn(new ArrayList<IAttribute>().iterator());
        List<IOperation> ops = operations();
        when(vpClass.operationIterator()).thenReturn(ops.iterator());

        MyElementPool pool = mock(MyElementPool.class);
        when(pool.getMyProject()).thenReturn(root);

        MyClassFactory factory = new MyClassFactory(pool);
        MyClass myClass = (MyClass) factory.reallyBuild(root, vpClass, stereotypes);

        assertEquals(2, myClass.getOperations().size());

        MyOperation open = myClass.getOperations().get(0);
        assertEquals("", open.getVisibility());
        assertEquals("open", open.getName());
        assertEquals("void", open.getReturnType());
        assertEquals(0, open.getParameters().size());

        MyOperation save = myClass.getOperations().get(1);
        assertEquals("private", save.getVisibility());
        assertEquals("boolean", save.getReturnType());
        assertEquals(1, save.getParameters().size());
        assertEquals("file", save.getParameters().get(0).getName());
        assertEquals("string", save.getParameters().get(0).getType());
    }
}
