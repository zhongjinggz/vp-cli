package plugins.vpcli.domain.outputmodel.pumlexporter.pumlclass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.model.*;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlDiagram;

/**
 * 针对 {@link PumlClassExporter} 的分支覆盖测试：
 * getBlockType、hasStereotype、以及属性/操作/参数格式化各分支。
 */
class PumlClassExporterTest {

    private final PumlClassExporter exporter = new PumlClassExporter();

    // ---------- getShapeType / getBlockType ----------

    @Test
    void getShapeTypeReturnsClassConstant() {
        assertEquals(IShapeTypeConstants.SHAPE_TYPE_CLASS, exporter.getShapeType());
    }

    @Test
    void blockTypeIsInterfaceWhenInterfaceStereotypePresent() {
        assertEquals("interface", exporter.getBlockType(clazzWithStereotypes("interface")));
    }

    @Test
    void blockTypeIsEnumWhenEnumerationStereotypePresent() {
        assertEquals("enum", exporter.getBlockType(clazzWithStereotypes("enumeration")));
    }

    @Test
    void blockTypeIsAbstractClassForAbstractClass() {
        IClass clazz = clazzWithStereotypes();
        when(clazz.isAbstract()).thenReturn(true);
        assertEquals("abstract class", exporter.getBlockType(clazz));
    }

    @Test
    void blockTypeIsPlainClassOtherwise() {
        assertEquals("class", exporter.getBlockType(clazzWithStereotypes()));
    }

    @Test
    void getBlockTypeThrowsForNonClassElement() {
        IModelElement notAClass = mock(IModelElement.class);
        assertThrows(Exception.class, () -> exporter.getBlockType(notAClass));
    }

    // ---------- attribute formatting branches ----------

    @Test
    void attributeWithStaticScopeTypeAndInitialValue() {
        IClass clazz = clazzWithAttributes(attr("count", "public", IAttribute.SCOPE_CLASSIFIER)
            .type("int").initialValue("0").build());
        assertEquals("+ {static} count: int = 0", body(clazz));
    }

    @Test
    void attributePrivateWithoutOptionalParts() {
        IClass clazz = clazzWithAttributes(attr("secret", "private", null).build());
        assertEquals("- secret", body(clazz));
    }

    @Test
    void attributeProtected() {
        IClass clazz = clazzWithAttributes(attr("p", "protected", null).build());
        assertEquals("# p", body(clazz));
    }

    @Test
    void attributePackage() {
        IClass clazz = clazzWithAttributes(attr("pk", "package", null).build());
        assertEquals("~ pk", body(clazz));
    }

    @Test
    void attributeNullAndUnknownVisibilityRenderNoSymbol() {
        IClass clazz = clazzWithAttributes(
            attr("a", null, null).build(),
            attr("b", "weird", null).build());
        assertEquals("a\nb", body(clazz));
    }

    @Test
    void attributeWithNullNameStillRendersTypeAndInitial() {
        IClass clazz = clazzWithAttributes(attr(null, "public", null).type("String")
            .initialValue("x").build());
        assertEquals("+ : String = x", body(clazz));
    }

    @Test
    void attributeWithOnlyName() {
        IClass clazz = clazzWithAttributes(attr("n", "public", null).build());
        assertEquals("+ n", body(clazz));
    }

    @Test
    void attributeWithTypeOnlyNoInitial() {
        IClass clazz = clazzWithAttributes(attr("n", "public", null).type("T").build());
        assertEquals("+ n: T", body(clazz));
    }

    @Test
    void attributeWithInitialValueOnlyAppended() {
        IClass clazz = clazzWithAttributes(attr("n", "public", null).initialValue("5").build());
        assertEquals("+ n = 5", body(clazz));
    }

    // ---------- operation formatting branches ----------

    @Test
    void abstractStaticOperationWithReturnType() {
        IOperation op = op("m").abstract_(true).static_(true).scope_(IOperation.SCOPE_CLASSIFIER).returnType("int").build();
        IClass clazz = clazzWithOperations(op);
        assertEquals("+ {abstract} {static} m(): int", body(clazz));
    }

    @Test
    void operationWithoutNameStillHasParentheses() {
        IClass clazz = clazzWithOperations(op(null).build());
        assertEquals("+ ()", body(clazz));
    }

    @Test
    void operationWithNoReturnType() {
        IClass clazz = clazzWithOperations(op("m").build());
        assertEquals("+ m()", body(clazz));
    }

    // ---------- parameter formatting branches ----------

    @Test
    void parametersWithNameTypeAndDefaultValue() {
        IOperation op = op("m")
            .param(prm("a", "int").defaultValue("1").build())
            .param(prm("b", "String").defaultValue("\"x\"").build())
            .build();
        IClass clazz = clazzWithOperations(op);
        assertEquals("+ m(a: int = 1, b: String = \"x\")", body(clazz));
    }

    @Test
    void parameterWithNameOnly() {
        IClass clazz = clazzWithOperations(op("m").param(prm("a", null).build()).build());
        assertEquals("+ m(a)", body(clazz));
    }

    @Test
    void parameterWithTypeButNoName() {
        IClass clazz = clazzWithOperations(op("m").param(prm(null, "T").build()).build());
        assertEquals("+ m(T)", body(clazz));
    }

    @Test
    void parameterWithDefaultValueButNoNameOrType() {
        IClass clazz = clazzWithOperations(op("m").param(prm(null, null).defaultValue("9").build()).build());
        assertEquals("+ m( = 9)", body(clazz));
    }

    // ---------- hasStereotype true path (via getBlockType) ----------

    @Test
    void stereotypeSearchContinuesUntilMatchingName() {
        IClass clazz = clazzWithStereotypes("other", "interface");
        assertEquals("interface", exporter.getBlockType(clazz));
    }

    @Test
    void nullNamedStereotypesSkippedWhenNoMatch() {
        IClass clazz = clazzWithStereotypes((String) null);
        assertEquals("class", exporter.getBlockType(clazz));
    }

    // ---------- enum literal rendering ----------

    @Test
    void enumLiteralsWithoutSeparatorWhenAlone() {
        IClass clazz = clazzWithEnumLiterals(literal("A"), literal("B"));
        assertEquals("A\nB", body(clazz));
    }

    @Test
    void nullOrEmptyEnumLiteralNamesAreSkipped() {
        IClass clazz = clazzWithEnumLiterals(literal("A"), literal(null), literal(""));
        assertEquals("A", body(clazz));
    }

    // ---------- helpers ----------

    private static String body(IClass clazz) {
        PumlDiagram diagram = new PumlDiagram("d");
        new PumlClassExporter().export(diagram, elementOf(clazz));
        String out = diagram.toString();
        int open = out.indexOf('{');
        int close = out.lastIndexOf('}');
        if (open < 0) {
            return out;
        }
        String inner = out.substring(open + 1, close);
        StringBuilder sb = new StringBuilder();
        for (String line : inner.split("\n", -1)) {
            if (sb.length() > 0) {
                sb.append('\n');
            }
            sb.append(line.strip());
        }
        return sb.toString().replaceAll("\\n+$", "").trim();
    }

    private static IClass clazzWithStereotypes(String... names) {
        IClass clazz = mock(IClass.class);
        List<IStereotype> stereos = new java.util.ArrayList<>();
        for (String n : names) {
            IStereotype s = mock(IStereotype.class);
            when(s.getName()).thenReturn(n);
            stereos.add(s);
        }
        when(clazz.stereotypeModelIterator()).thenAnswer(inv -> stereos.iterator());
        when(clazz.toStereotypeModelArray()).thenReturn(stereos.toArray(new IStereotype[stereos.size()]));
        return clazz;
    }

    private static IClass clazzWithAttributes(IAttribute... attrs) {
        return clazzWithLists(java.util.Arrays.asList(attrs), Collections.emptyList(),
            Collections.emptyList());
    }

    private static IClass clazzWithOperations(IOperation... ops) {
        return clazzWithLists(Collections.emptyList(), java.util.Arrays.asList(ops),
            Collections.emptyList());
    }

    private static IClass clazzWithEnumLiterals(IEnumerationLiteral... literals) {
        return clazzWithLists(Collections.emptyList(), Collections.emptyList(),
            java.util.Arrays.asList(literals));
    }

    private static IClass clazzWithLists(List<IAttribute> attrs, List<IOperation> ops,
                                         List<IEnumerationLiteral> literals) {
        IClass clazz = mock(IClass.class);
        when(clazz.attributeIterator()).thenAnswer(inv -> attrs.iterator());
        when(clazz.operationIterator()).thenAnswer(inv -> ops.iterator());
        when(clazz.enumerationLiteralIterator()).thenAnswer(inv -> literals.iterator());
        when(clazz.stereotypeModelIterator()).thenAnswer(inv -> Collections.emptyIterator());
        return clazz;
    }

    private static AttrBuilder attr(String name, String visibility, String scope) {
        return new AttrBuilder(name, visibility, scope);
    }

    private static OpBuilder op(String name) {
        return new OpBuilder(name);
    }

    private static ParamBuilder prm(String name, String type) {
        return new ParamBuilder(name, type);
    }

    private static IEnumerationLiteral literal(String name) {
        IEnumerationLiteral l = mock(IEnumerationLiteral.class);
        when(l.getName()).thenReturn(name);
        return l;
    }

    private static IDiagramElement elementOf(IModelElement model) {
        IShapeUIModel s = mock(IShapeUIModel.class);
        when(s.getModelElement()).thenReturn(model);
        when(s.toChildArray()).thenReturn(new IShapeUIModel[0]);
        return s;
    }

    // ---- builders to configure mocks compactly ----

    private static final class AttrBuilder {
        private final String name;
        private final String visibility;
        private final String scope;
        private String type = null;
        private String initialValue = null;

        AttrBuilder(String name, String visibility, String scope) {
            this.name = name;
            this.visibility = visibility;
            this.scope = scope;
        }

        AttrBuilder type(String t) {
            this.type = t;
            return this;
        }

        AttrBuilder initialValue(String v) {
            this.initialValue = v;
            return this;
        }

        IAttribute build() {
            IAttribute a = mock(IAttribute.class);
            when(a.getVisibility()).thenReturn(visibility);
            when(a.getScope()).thenReturn(scope);
            when(a.getName()).thenReturn(name);
            when(a.getTypeAsString()).thenReturn(type);
            when(a.getInitialValueAsString()).thenReturn(initialValue);
            return a;
        }
    }

    private static final class OpBuilder {
        private final String name;
        private boolean abs;
        private boolean stat;
        private String scope;
        private String returnType = null;
        private final java.util.List<IParameter> params = new java.util.ArrayList<>();

        OpBuilder(String name) {
            this.name = name;
        }

        OpBuilder abstract_(boolean v) {
            this.abs = v;
            return this;
        }

        OpBuilder static_(boolean v) {
            this.stat = v;
            return this;
        }

        OpBuilder scope_(String s) {
            this.scope = s;
            return this;
        }

        OpBuilder returnType(String t) {
            this.returnType = t;
            return this;
        }

        OpBuilder param(IParameter p) {
            this.params.add(p);
            return this;
        }

        IOperation build() {
            IOperation o = mock(IOperation.class);
            when(o.getName()).thenReturn(name);
            when(o.isAbstract()).thenReturn(abs);
            when(o.isStatic()).thenReturn(stat);
            when(o.getScope()).thenReturn(scope);
            when(o.getReturnTypeAsString()).thenReturn(returnType);
            when(o.parameterCount()).thenReturn(params.size());
            for (int i = 0; i < params.size(); i++) {
                when(o.getParameterByIndex(i)).thenReturn(params.get(i));
            }
            when(o.getVisibility()).thenReturn("public");
            return o;
        }
    }

    private static final class ParamBuilder {
        private final String name;
        private final String type;
        private String defaultValue = null;

        ParamBuilder(String name, String type) {
            this.name = name;
            this.type = type;
        }

        ParamBuilder defaultValue(String v) {
            this.defaultValue = v;
            return this;
        }

        IParameter build() {
            IParameter p = mock(IParameter.class);
            when(p.getName()).thenReturn(name);
            when(p.getTypeAsString()).thenReturn(type);
            when(p.getDefaultValueAsString()).thenReturn(defaultValue);
            return p;
        }
    }
}
