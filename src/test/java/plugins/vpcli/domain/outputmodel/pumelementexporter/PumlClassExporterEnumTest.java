package plugins.vpcli.domain.outputmodel.pumelementexporter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IEnumerationLiteral;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IOperation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.outputmodel.PumlDiagram;

/**
 * 步 #20：枚举类的分区渲染（`--` 分隔属性/操作/枚举值区）。
 * 规则：只有单区时无 `--`，两区及以上时各区之间以 `--` 分隔。
 */
class PumlClassExporterEnumTest {

    @Test
    void onlyAttributesNoSeparator() {
        IClass clazz = clazzWith(Arrays.asList(attr("a1"), attr("a2")),
            Collections.emptyList(), Collections.emptyList());

        assertEquals("+ a1\n+ a2", body(clazz));
    }

    @Test
    void attributesAndOperationsSeparatedByDoubleDash() {
        IClass clazz = clazzWith(
            Arrays.asList(attr("a1")),
            Arrays.asList(op("m1")),
            Collections.emptyList());

        assertEquals("+ a1\n--\n+ m1()", body(clazz));
    }

    @Test
    void attributesOperationsAndEnumLiteralsTwoSeparators() {
        IClass clazz = clazzWith(
            Arrays.asList(attr("a1")),
            Arrays.asList(op("m1")),
            Arrays.asList(literal("NEW"), literal("CLOSED")));

        assertEquals("+ a1\n--\n+ m1()\n--\nNEW\nCLOSED", body(clazz));
    }

    @Test
    void onlyEnumLiteralsNoSeparator() {
        IClass clazz = clazzWith(Collections.emptyList(), Collections.emptyList(),
            Arrays.asList(literal("NEW"), literal("ACTIVE"), literal("CLOSED")));

        assertEquals("NEW\nACTIVE\nCLOSED", body(clazz));
    }

    @Test
    void operationsAndEnumLiteralsSeparatedByDoubleDash() {
        IClass clazz = clazzWith(Collections.emptyList(), Arrays.asList(op("m1")),
            Arrays.asList(literal("A")));

        assertEquals("+ m1()\n--\nA", body(clazz));
    }

    @Test
    void emptyClassBodyIsEmpty() {
        IClass clazz = clazzWith(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        assertEquals("", body(clazz));
    }

    // ---- helpers ----

    private static String body(IClass clazz) {
        PumlDiagram diagram = new PumlDiagram("d");
        new PumlClassExporter().export(diagram, elementOf(clazz));
        String out = diagram.toString();
        int open = out.indexOf('{');
        int close = out.lastIndexOf('}');
        String inner = out.substring(open + 1, close);
        // 每行去除前导缩进与首尾换行
        StringBuilder sb = new StringBuilder();
        for (String line : inner.split("\n", -1)) {
            if (sb.length() > 0) {
                sb.append('\n');
            }
            sb.append(line.strip());
        }
        return sb.toString().replaceAll("\\n+$", "").trim();
    }

    private static IClass clazzWith(List<IAttribute> attrs, List<IOperation> ops,
                                    List<IEnumerationLiteral> literals) {
        IClass clazz = mock(IClass.class);
        when(clazz.attributeIterator()).thenAnswer(inv -> attrs.iterator());
        when(clazz.operationIterator()).thenAnswer(inv -> ops.iterator());
        when(clazz.enumerationLiteralIterator()).thenAnswer(inv -> literals.iterator());
        when(clazz.stereotypeModelIterator()).thenAnswer(inv -> Collections.emptyIterator());
        return clazz;
    }

    private static IAttribute attr(String name) {
        IAttribute a = mock(IAttribute.class);
        when(a.getName()).thenReturn(name);
        when(a.getTypeAsString()).thenReturn("");
        when(a.getInitialValueAsString()).thenReturn("");
        when(a.getVisibility()).thenReturn("public");
        when(a.getScope()).thenReturn(IAttribute.SCOPE_INSTANCE);
        return a;
    }

    private static IOperation op(String name) {
        IOperation o = mock(IOperation.class);
        when(o.getName()).thenReturn(name);
        when(o.isAbstract()).thenReturn(false);
        when(o.isStatic()).thenReturn(false);
        when(o.parameterCount()).thenReturn(0);
        when(o.getReturnTypeAsString()).thenReturn("");
        when(o.getVisibility()).thenReturn("public");
        return o;
    }

    private static IEnumerationLiteral literal(String name) {
        IEnumerationLiteral l = mock(IEnumerationLiteral.class);
        when(l.getName()).thenReturn(name);
        return l;
    }

    private static com.vp.plugin.diagram.IDiagramElement elementOf(IModelElement model) {
        com.vp.plugin.diagram.IShapeUIModel s = mock(com.vp.plugin.diagram.IShapeUIModel.class);
        when(s.getModelElement()).thenReturn(model);
        when(s.toChildArray()).thenReturn(new com.vp.plugin.diagram.IShapeUIModel[0]);
        return s;
    }
}
