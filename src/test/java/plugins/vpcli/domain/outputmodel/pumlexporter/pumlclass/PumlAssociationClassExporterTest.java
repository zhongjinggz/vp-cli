package plugins.vpcli.domain.outputmodel.pumlexporter.pumlclass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.vp.plugin.diagram.IConnectorUIModel;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationClass;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlDiagram;

/**
 * 针对 {@link PumlAssociationClassExporter}：把「关联类 ↔ 关联线」的虚连线
 * 渲染为 `(alias1, alias2) .. associationClassAlias`。
 */
class PumlAssociationClassExporterTest {

    private final PumlAssociationClassExporter exporter = new PumlAssociationClassExporter();

    @Test
    void getShapeTypeReturnsAssociationClassConstant() {
        assertEquals(IShapeTypeConstants.SHAPE_TYPE_ASSOCIATION_CLASS, exporter.getShapeType());
    }

    @Test
    void associationClassLinksParticipantsToClass() {
        IConnectorUIModel connector = connectorWith(associationClass(association(
            end("company"), end("person")), classElement("employment")));

        assertTrue(renders(connector, "(company, person) .. employment"));
    }

    @Test
    void handlesReversedAssociationEndpoints() {
        // 关联线模型端点 from=关联类、to=关联 的情况同样支持。
        IConnectorUIModel connector = connectorWith(associationClass(classElement("employment"),
            association(end("company"), end("person"))));

        assertTrue(renders(connector, "(company, person) .. employment"));
    }

    // ---------- helpers ----------

    private static boolean renders(IConnectorUIModel connector, String expected) {
        PumlDiagram diagram = new PumlDiagram("d");
        new PumlAssociationClassExporter().export(diagram, connector);
        return diagram.toString().contains(expected);
    }

    private static IConnectorUIModel connectorWith(IAssociationClass model) {
        IConnectorUIModel c = mock(IConnectorUIModel.class);
        when(c.getModelElement()).thenReturn(model);
        return c;
    }

    private static IAssociationClass associationClass(IModelElement from, IModelElement to) {
        IAssociationClass ac = mock(IAssociationClass.class);
        when(ac.getFrom()).thenReturn(from);
        when(ac.getTo()).thenReturn(to);
        return ac;
    }

    private static IAssociation association(IAssociationEnd from, IAssociationEnd to) {
        IAssociation a = mock(IAssociation.class);
        when(a.getFromEnd()).thenReturn(from);
        when(a.getToEnd()).thenReturn(to);
        return a;
    }

    private static IAssociationEnd end(String alias) {
        IAssociationEnd e = mock(IAssociationEnd.class);
        IModelElement m = mock(IModelElement.class);
        when(m.getName()).thenReturn(alias);
        when(m.getParent()).thenReturn(null);
        when(e.getModelElement()).thenReturn(m);
        return e;
    }

    private static IClass classElement(String name) {
        IClass c = mock(IClass.class);
        when(c.getName()).thenReturn(name);
        when(c.getParent()).thenReturn(null);
        when(c.stereotypeModelIterator()).thenReturn(Collections.emptyIterator());
        when(c.attributeIterator()).thenReturn(Collections.emptyIterator());
        when(c.operationIterator()).thenReturn(Collections.emptyIterator());
        when(c.enumerationLiteralIterator()).thenReturn(Collections.emptyIterator());
        return c;
    }
}
