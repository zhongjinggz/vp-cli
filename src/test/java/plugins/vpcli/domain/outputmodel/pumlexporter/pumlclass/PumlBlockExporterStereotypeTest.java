package plugins.vpcli.domain.outputmodel.pumlexporter.pumlclass;

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
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlDiagram;
import plugins.vpcli.domain.outputmodel.pumlexporter.pumlnamespace.PumlModelExporter;
import plugins.vpcli.domain.outputmodel.pumlexporter.pumlnamespace.PumlPackageExporter;

/**
 * 步 #15：类、包的泛型 Stereotype 渲染。
 * 说明：mockito 禁止在外层 when() 的参数表达式里再调用 when()，
 * 故所有 stereotype mock 均先构建好，再开始 stubbing。
 */
class PumlBlockExporterStereotypeTest {

    // ---- getStereotypesText：泛型名原样输出、不净化 ----

    @Test
    void noStereotypeYieldsEmpty() {
        PumlClassExporter exporter = new PumlClassExporter();
        IModelElement vp = mock(IModelElement.class);
        when(vp.stereotypeModelIterator()).thenReturn(Collections.emptyIterator());

        assertEquals("", exporter.getStereotypesText(vp));
    }

    @Test
    void singleStereotypeRenderedRaw() {
        PumlClassExporter exporter = new PumlClassExporter();
        IStereotype s = stereo("service");
        IModelElement vp = mock(IModelElement.class);
        when(vp.stereotypeModelIterator()).thenReturn(iterOf(s));
        when(vp.toStereotypeModelArray()).thenReturn(new IStereotype[]{s});

        assertEquals("<<service>>", exporter.getStereotypesText(vp));
    }

    @Test
    void multipleStereotypesSpaceSeparated() {
        PumlClassExporter exporter = new PumlClassExporter();
        IStereotype a = stereo("service");
        IStereotype b = stereo("repository");
        IModelElement vp = mock(IModelElement.class);
//        when(vp.stereotypeModelIterator()).thenReturn(iterOf(a, b));
        when(vp.toStereotypeModelArray()).thenReturn(new IStereotype[]{a, b});

        assertEquals("<<service>> <<repository>>", exporter.getStereotypesText(vp));
    }

    @Test
    void stereotypeNameWithSpacesAndPunctuationKeptAsIs() {
        PumlClassExporter exporter = new PumlClassExporter();
        IStereotype s = stereo("my service");
        IModelElement vp = mock(IModelElement.class);
        when(vp.stereotypeModelIterator()).thenReturn(iterOf(s));
        when(vp.toStereotypeModelArray()).thenReturn(new IStereotype[]{s});

        assertEquals("<<my service>>", exporter.getStereotypesText(vp));
    }

    @Test
    void nullStereotypeNameSkipped() {
        PumlClassExporter exporter = new PumlClassExporter();
        IStereotype empty = mock(IStereotype.class);
        when(empty.getName()).thenReturn(null);
        IModelElement vp = mock(IModelElement.class);
        when(vp.stereotypeModelIterator()).thenReturn(iterOf(empty));

        assertEquals("", exporter.getStereotypesText(vp));
    }

    // ---- keywordAndStereotypes：固定原型在前 + 泛型在后 ----

    @Test
    void classSuffixOnlyFromStereotypes() {
        PumlClassExporter exporter = new PumlClassExporter();
        IStereotype s = stereo("service");
        IModelElement vp = mock(IModelElement.class);
        when(vp.stereotypeModelIterator()).thenReturn(iterOf(s));
        when(vp.toStereotypeModelArray()).thenReturn(new IStereotype[]{s});

        assertEquals("<<service>>", exporter.keywordAndStereotypes(vp));
    }

    @Test
    void modelSuffixKeepsFixedPrototypeThenStereotypes() {
        PumlModelExporter exporter = new PumlModelExporter();
        IStereotype s = stereo("framework");
        IModelElement vp = mock(IModelElement.class);
        when(vp.stereotypeModelIterator()).thenReturn(iterOf(s));
        when(vp.toStereotypeModelArray()).thenReturn(new IStereotype[]{s});

        assertEquals("<<model>> <<framework>>", exporter.keywordAndStereotypes(vp));
    }

    @Test
    void modelSuffixKeepsFixedPrototypeWhenNoStereotype() {
        PumlModelExporter exporter = new PumlModelExporter();
        IModelElement vp = mock(IModelElement.class);
        when(vp.stereotypeModelIterator()).thenReturn(Collections.emptyIterator());

        assertEquals("<<model>>", exporter.keywordAndStereotypes(vp));
    }

    @Test
    void packageNoStereotypeEmptySuffix() {
        PumlPackageExporter exporter = new PumlPackageExporter();
        IModelElement vp = mock(IModelElement.class);
        when(vp.stereotypeModelIterator()).thenReturn(Collections.emptyIterator());

        assertEquals("", exporter.keywordAndStereotypes(vp));
    }

    // ---- getBlockType：抽象类 ----

    @Test
    void abstractClassYieldsAbstractClassKeyword() {
        PumlClassExporter exporter = new PumlClassExporter();
        IClass clazz = mock(IClass.class);
        when(clazz.isAbstract()).thenReturn(true);
        when(clazz.stereotypeModelIterator()).thenReturn(Collections.emptyIterator());

        assertEquals("abstract class", exporter.getBlockType(clazz));
    }

    @Test
    void concreteClassYieldsClassKeyword() {
        PumlClassExporter exporter = new PumlClassExporter();
        IClass clazz = mock(IClass.class);
        when(clazz.isAbstract()).thenReturn(false);
        when(clazz.stereotypeModelIterator()).thenReturn(Collections.emptyIterator());

        assertEquals("class", exporter.getBlockType(clazz));
    }

    // ---- getBlockType：接口（#18） ----

    @Test
    void interfaceClassYieldsInterfaceKeyword() {
        PumlClassExporter exporter = new PumlClassExporter();
        IStereotype s = stereo("interface");
        IClass clazz = mock(IClass.class);
        when(clazz.isAbstract()).thenReturn(false);
        when(clazz.stereotypeModelIterator()).thenReturn(iterOf(s));
        when(clazz.toStereotypeModelArray()).thenReturn(new IStereotype[]{s});

        assertEquals("interface", exporter.getBlockType(clazz));
    }

    @Test
    void interfaceKeywordPrecedesAbstractCheck() {
        PumlClassExporter exporter = new PumlClassExporter();
        IStereotype s = stereo("interface");
        IClass clazz = mock(IClass.class);
        when(clazz.isAbstract()).thenReturn(true);
        when(clazz.stereotypeModelIterator()).thenReturn(iterOf(s));
        when(clazz.toStereotypeModelArray()).thenReturn(new IStereotype[]{s});

        assertEquals("interface", exporter.getBlockType(clazz));
    }

    // ---- getBlockType：枚举（#20） ----

    @Test
    void enumerationClassYieldsEnumKeyword() {
        PumlClassExporter exporter = new PumlClassExporter();
        IStereotype s = stereo("enumeration");
        IClass clazz = mock(IClass.class);
        when(clazz.isAbstract()).thenReturn(false);
        when(clazz.stereotypeModelIterator()).thenAnswer(inv -> iterOf(s));
        when(clazz.toStereotypeModelArray()).thenReturn(new IStereotype[]{s});

        assertEquals("enum", exporter.getBlockType(clazz));
    }

    @Test
    void interfaceKeywordTakesPrecedenceOverEnumeration() {
        PumlClassExporter exporter = new PumlClassExporter();
        IStereotype iff = stereo("interface");
        IStereotype en = stereo("enumeration");
        IClass clazz = mock(IClass.class);
        when(clazz.isAbstract()).thenReturn(false);
        when(clazz.stereotypeModelIterator()).thenAnswer(inv -> iterOf(iff, en));
        when(clazz.toStereotypeModelArray()).thenReturn(new IStereotype[] {iff, en});



        assertEquals("interface", exporter.getBlockType(clazz));
    }

    @Test
    void packageKeepsNamespaceType() {
        PumlPackageExporter exporter = new PumlPackageExporter();
        IPackage pkg = mock(IPackage.class);

        assertEquals("namespace", exporter.getBlockType(pkg));
        assertEquals("namespace", "namespace");
    }

    // ---- 端到端：beginBlock 输出块头 ----

    @Test
    void classDeclaresStereotypeAfterAlias() {
        PumlDiagram diagram = new PumlDiagram("d");
        IStereotype s = stereo("service");
        IClass clazz = model(IClass.class, "class1", null);
        when(clazz.isAbstract()).thenReturn(false);
        when(clazz.stereotypeModelIterator()).thenAnswer(inv -> iterOf(s));
        when(clazz.toStereotypeModelArray()).thenReturn(new IStereotype[]{s});

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
        when(clazz.stereotypeModelIterator()).thenAnswer(inv -> iterOf(s));
        when(clazz.toStereotypeModelArray()).thenReturn(new IStereotype[]{s});

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
        when(pkg.toStereotypeModelArray()).thenReturn(new IStereotype[]{s});

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
            when(((IClass) m).enumerationLiteralIterator()).thenReturn(Collections.emptyIterator());
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
