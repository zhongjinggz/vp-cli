package plugins.vpcli.domain.outputmodel.pumelementexporter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.IStereotype;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.outputmodel.PumlDiagram;

/**
 * 步 #15：类、包的泛型 Stereotype 渲染。
 * 说明：mockito 禁止在外层 when() 的参数表达式里再调用 when()，
 * 故所有 stereotype mock 均先构建好，再开始 stubbing。
 */
class PumlBlockExporterStereotypeTest {

    // ---- renderStereotypes：泛型名原样输出、不净化 ----

    @Test
    void noStereotypeYieldsEmpty() {
        PumlClassExporter exporter = new PumlClassExporter();
        IModelElement vp = mock(IModelElement.class);
        when(vp.stereotypeModelIterator()).thenReturn(Collections.emptyIterator());

        assertEquals("", exporter.renderStereotypes(vp));
    }

    @Test
    void singleStereotypeRenderedRaw() {
        PumlClassExporter exporter = new PumlClassExporter();
        IStereotype s = stereo("service");
        IModelElement vp = mock(IModelElement.class);
        when(vp.stereotypeModelIterator()).thenReturn(iterOf(s));

        assertEquals("<<service>>", exporter.renderStereotypes(vp));
    }

    @Test
    void multipleStereotypesSpaceSeparated() {
        PumlClassExporter exporter = new PumlClassExporter();
        IStereotype a = stereo("service");
        IStereotype b = stereo("repository");
        IModelElement vp = mock(IModelElement.class);
        when(vp.stereotypeModelIterator()).thenReturn(iterOf(a, b));

        assertEquals("<<service>> <<repository>>", exporter.renderStereotypes(vp));
    }

    @Test
    void stereotypeNameWithSpacesAndPunctuationKeptAsIs() {
        PumlClassExporter exporter = new PumlClassExporter();
        IStereotype s = stereo("my service");
        IModelElement vp = mock(IModelElement.class);
        when(vp.stereotypeModelIterator()).thenReturn(iterOf(s));

        assertEquals("<<my service>>", exporter.renderStereotypes(vp));
    }

    @Test
    void nullStereotypeNameSkipped() {
        PumlClassExporter exporter = new PumlClassExporter();
        IStereotype empty = mock(IStereotype.class);
        when(empty.getName()).thenReturn(null);
        IModelElement vp = mock(IModelElement.class);
        when(vp.stereotypeModelIterator()).thenReturn(iterOf(empty));

        assertEquals("", exporter.renderStereotypes(vp));
    }

    // ---- buildBlockSuffix：固定原型在前 + 泛型在后 ----

    @Test
    void classSuffixOnlyFromStereotypes() {
        PumlClassExporter exporter = new PumlClassExporter();
        IStereotype s = stereo("service");
        IModelElement vp = mock(IModelElement.class);
        when(vp.stereotypeModelIterator()).thenReturn(iterOf(s));

        assertEquals("<<service>>", exporter.buildBlockSuffix(vp));
    }

    @Test
    void modelSuffixKeepsFixedPrototypeThenStereotypes() {
        PumlModelExporter exporter = new PumlModelExporter();
        IStereotype s = stereo("framework");
        IModelElement vp = mock(IModelElement.class);
        when(vp.stereotypeModelIterator()).thenReturn(iterOf(s));

        assertEquals("<<model>> <<framework>>", exporter.buildBlockSuffix(vp));
    }

    @Test
    void modelSuffixKeepsFixedPrototypeWhenNoStereotype() {
        PumlModelExporter exporter = new PumlModelExporter();
        IModelElement vp = mock(IModelElement.class);
        when(vp.stereotypeModelIterator()).thenReturn(Collections.emptyIterator());

        assertEquals("<<model>>", exporter.buildBlockSuffix(vp));
    }

    @Test
    void packageNoStereotypeEmptySuffix() {
        PumlPackageExporter exporter = new PumlPackageExporter();
        IModelElement vp = mock(IModelElement.class);
        when(vp.stereotypeModelIterator()).thenReturn(Collections.emptyIterator());

        assertEquals("", exporter.buildBlockSuffix(vp));
    }

    // ---- buildBlockType：抽象类 ----

    @Test
    void abstractClassYieldsAbstractClassKeyword() {
        PumlClassExporter exporter = new PumlClassExporter();
        IClass clazz = mock(IClass.class);
        when(clazz.isAbstract()).thenReturn(true);

        assertEquals("abstract class", exporter.buildBlockType(clazz));
    }

    @Test
    void concreteClassYieldsClassKeyword() {
        PumlClassExporter exporter = new PumlClassExporter();
        IClass clazz = mock(IClass.class);
        when(clazz.isAbstract()).thenReturn(false);

        assertEquals("class", exporter.buildBlockType(clazz));
    }

    @Test
    void packageKeepsNamespaceType() {
        PumlPackageExporter exporter = new PumlPackageExporter();
        IPackage pkg = mock(IPackage.class);

        assertEquals("namespace", exporter.buildBlockType(pkg));
        assertEquals("namespace", exporter.getBlockType());
    }

    // ---- 端到端：beginBlock 输出块头 ----

    @Test
    void classDeclaresStereotypeAfterAlias() {
        PumlDiagram diagram = new PumlDiagram("d");
        IStereotype s = stereo("service");
        IClass clazz = model(IClass.class, "class1", null);
        when(clazz.isAbstract()).thenReturn(false);
        when(clazz.stereotypeModelIterator()).thenReturn(iterOf(s));

        new PumlClassExporter().export(diagram, elementOf(clazz, false));

        String out = diagram.toString();
        assertTrue(out.contains("class \"class1\" as class1 <<service>> {"),
            () -> "实际输出：\n" + out);
    }

    @Test
    void abstractClassDeclaresAbstractKeywordAndStereotype() {
        PumlDiagram diagram = new PumlDiagram("d");
        IStereotype s = stereo("repository");
        IClass clazz = model(IClass.class, "class2", null);
        when(clazz.isAbstract()).thenReturn(true);
        when(clazz.stereotypeModelIterator()).thenReturn(iterOf(s));

        new PumlClassExporter().export(diagram, elementOf(clazz, false));

        String out = diagram.toString();
        assertTrue(out.contains("abstract class \"class2\" as class2 <<repository>> {"),
            () -> "实际输出：\n" + out);
    }

    @Test
    void packageDeclaresStereotypeAfterAlias() {
        PumlDiagram diagram = new PumlDiagram("d");
        IStereotype s = stereo("framework");
        IPackage pkg = model(IPackage.class, "package1", null);
        when(pkg.stereotypeModelIterator()).thenReturn(iterOf(s));

        new PumlPackageExporter().export(diagram, elementOf(pkg, false));

        String out = diagram.toString();
        assertTrue(out.contains("namespace \"package1\" as package1 <<framework>> {"),
            () -> "实际输出：\n" + out);
    }

    @Test
    void classWithNoStereotypeHasNoExtraSuffix() {
        PumlDiagram diagram = new PumlDiagram("d");
        IClass clazz = model(IClass.class, "classNoRel", null);
        when(clazz.isAbstract()).thenReturn(false);
        when(clazz.stereotypeModelIterator()).thenReturn(Collections.emptyIterator());

        new PumlClassExporter().export(diagram, elementOf(clazz, false));

        String out = diagram.toString();
        assertTrue(out.contains("class \"classNoRel\" as classNoRel {"),
            () -> "实际输出：\n" + out);
    }

    // ---- helpers ----

    private static IStereotype stereo(String name) {
        IStereotype s = mock(IStereotype.class);
        when(s.getName()).thenReturn(name);
        return s;
    }

    private static Iterator<?> iterOf(IStereotype... stereotypes) {
        List<IStereotype> list = Arrays.asList(stereotypes);
        return list.iterator();
    }

    private static <T extends IModelElement> T model(Class<T> type, String name,
                                                     IModelElement parent) {
        T m = mock(type);
        when(m.getName()).thenReturn(name);
        when(m.getParent()).thenReturn(parent);
        if (m instanceof IClass) {
            when(((IClass) m).attributeIterator()).thenReturn(Collections.emptyIterator());
            when(((IClass) m).operationIterator()).thenReturn(Collections.emptyIterator());
        }
        return m;
    }

    private static com.vp.plugin.diagram.IDiagramElement elementOf(IModelElement model,
                                                                   boolean shape) {
        if (shape) {
            IShapeUIModel s = mock(IShapeUIModel.class);
            when(s.getModelElement()).thenReturn(model);
            return s;
        }
        com.vp.plugin.diagram.IDiagramElement e =
            mock(com.vp.plugin.diagram.IDiagramElement.class);
        when(e.getModelElement()).thenReturn(model);
        return e;
    }
}
