package plugins.vpcli.domain.outputmodel.pumlexporter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.model.IModelElement;
import org.junit.jupiter.api.Test;

/**
 * 针对 {@link PumlElementExporter#pumlAlias}：验证全局别名算法
 * `别名 := <类型>__<名称>__<模型ID>__<图ID>`（各段以两个下划线分隔），
 * 无名元素忽略名称段，图元素ID为 null 时省略图ID段；连字符替换为下划线。
 */
class PumlElementAliasTest {

    private static IModelElement namedElement(String type, String name, String modelId) {
        IModelElement m = mock(IModelElement.class);
        when(m.getModelType()).thenReturn(type);
        when(m.getName()).thenReturn(name);
        when(m.getId()).thenReturn(modelId);
        return m;
    }

    private static IModelElement unnamedElement(String type, String modelId) {
        IModelElement m = mock(IModelElement.class);
        when(m.getModelType()).thenReturn(type);
        when(m.getName()).thenReturn("");
        when(m.getId()).thenReturn(modelId);
        return m;
    }

    private static IDiagramElement element(IModelElement model, String diagramId) {
        IDiagramElement e = mock(IDiagramElement.class);
        when(e.getModelElement()).thenReturn(model);
        when(e.getId()).thenReturn(diagramId);
        return e;
    }

    @Test
    void namedElementWithDiagramIdProducesFourSegmentAlias() {
        IModelElement clazz = namedElement("Class", "A", "12345");
        assertEquals("Class__A__12345__abcde",
            PumlElementExporter.pumlAlias(element(clazz, "abcde")));
    }

    @Test
    void unnamedElementIgnoresNameSegment() {
        IModelElement nary = unnamedElement("NaryAssociation", "Ia_N9YWD_AAAAQn2");
        assertEquals("NaryAssociation__Ia_N9YWD_AAAAQn2__1234",
            PumlElementExporter.pumlAlias(element(nary, "1234")));
    }

    @Test
    void nullDiagramIdOmitsDiagramSegment() {
        IModelElement clazz = namedElement("Class", "A", "12345");
        assertEquals("Class__A__12345",
            PumlElementExporter.pumlAlias(element(clazz, null)));
    }

    @Test
    void hyphenInNameIsReplacedByUnderscore() {
        IModelElement clazz = namedElement("Class", "My-Type", "12345");
        assertEquals("Class__My_Type__12345__abcde",
            PumlElementExporter.pumlAlias(element(clazz, "abcde")));
    }

    @Test
    void underscoreAndNonAlnumAreReplaced() {
        IModelElement clazz = namedElement("Class", "My Name", "123.45");
        assertEquals("Class__My_Name__123_45__a_bc",
            PumlElementExporter.pumlAlias(element(clazz, "a bc")));
    }

    // ---- buildAliasFromSegments (56-65 行) 的分段拼接与字符清洗规则 ----

    @Test
    void emptyTypeNameAndDiagramIdYieldEmptyAlias() {
        IModelElement m = mock(IModelElement.class);
        when(m.getModelType()).thenReturn("");
        when(m.getName()).thenReturn("");
        when(m.getId()).thenReturn("");
        assertEquals("", PumlElementExporter.pumlAlias(element(m, null)));
    }

    @Test
    void singleSegmentAliasHasNoSeparator() {
        IModelElement m = namedElement("Class", "", null);
        assertEquals("Class", PumlElementExporter.pumlAlias(element(m, null)));
    }

    @Test
    void cjkCharactersInSegmentsArePreserved() {
        IModelElement m = namedElement("Class", "用户", "模型ID");
        assertEquals("Class__用户__模型ID",
            PumlElementExporter.pumlAlias(element(m, null)));
    }

    @Test
    void mixedIllegalCharactersAreEachReplacedWithUnderscore() {
        IModelElement m = namedElement("Class", "a b.c-d", "x;y");
        assertEquals("Class__a_b_c_d__x_y",
            PumlElementExporter.pumlAlias(element(m, null)));
    }
}
