package plugins.vpcli.domain.outputmodel.pumlexporter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.vp.plugin.model.IModelElement;
import org.junit.jupiter.api.Test;

/**
 * 针对 {@link PumlElementExporter#buildAlias}：验证全局别名算法
 * `别名 := <类型>__<名称>__<模型ID>__<图ID>`（各段以两个下划线分隔），
 * 无名元素忽略名称段，图元素ID为 null 时省略图ID段。
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

    @Test
    void namedElementWithDiagramIdProducesFourSegmentAlias() {
        IModelElement clazz = namedElement("Class", "A", "12345");
        assertEquals("Class__A__12345__abcde",
            PumlElementExporter.buildAlias(clazz, "abcde"));
    }

    @Test
    void unnamedElementIgnoresNameSegment() {
        IModelElement nary = unnamedElement("NaryAssociation", "Ia_N9YWD_AAAAQn2");
        assertEquals("NaryAssociation__Ia_N9YWD_AAAAQn2__1234",
            PumlElementExporter.buildAlias(nary, "1234"));
    }

    @Test
    void nullDiagramIdOmitsDiagramSegment() {
        IModelElement clazz = namedElement("Class", "A", "12345");
        assertEquals("Class__A__12345", PumlElementExporter.buildAlias(clazz, null));
    }
}
