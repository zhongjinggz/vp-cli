package plugins.vpcli.domain.outputmodel.pumlexporter.pumlclass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.INARY;
import com.vp.plugin.model.IRelationshipEnd;
import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlDiagram;

/**
 * 针对 {@link PumlNAryExporter}：把 n-ary 关联的枢纽渲染为
 * `diamond <别名>`，各参与类以带多重性的实线 `--` 连到菱形。
 */
class PumlNAryExporterTest {

    private final PumlNAryExporter exporter = new PumlNAryExporter();

    @Test
    void getShapeTypeReturnsNaryConstant() {
        assertEquals(IShapeTypeConstants.SHAPE_TYPE_NARY, exporter.getShapeType());
    }

//    @Test
//    void exportsDiamondAndParticipantLinks() {
//        INARY nary = nary(
//            participant("A", "0..*"),
//            participant("B", "0..*"),
//            participant("C", "0..*"));
//
//        PumlDiagram diagram = export(elementOf(nary));
//        String text = diagram.toString();
//        assertTrue(text.contains("diamond nary1"));
//        assertTrue(text.contains("A \"0..*\" -- nary1"));
//        assertTrue(text.contains("B \"0..*\" -- nary1"));
//        assertTrue(text.contains("C \"0..*\" -- nary1"));
//    }

//    @Test
//    void unspecifiedMultiplicityOmitsLabel() {
//        INARY nary = nary(
//            participant("A", IAssociationEnd.MULTIPLICITY_UNSPECIFIED));
//
//        PumlDiagram diagram = export(elementOf(nary));
//        assertTrue(diagram.toString().contains("diamond nary1"));
//        assertTrue(diagram.toString().contains("A -- nary1"));
//    }

//    @Test
//    void ignoresNullParticipants() {
//        INARY nary = nary(participant("A", "1"), null);
//
//        PumlDiagram diagram = export(elementOf(nary));
//        String text = diagram.toString();
//        assertTrue(text.contains("diamond nary1"));
//        assertTrue(text.contains("A \"1\" -- nary1"));
//    }

    // ---------- helpers ----------

    private PumlDiagram export(IDiagramElement element) {
        PumlDiagram diagram = new PumlDiagram("d");
        exporter.export(diagram, element);
        return diagram;
    }

    private static IDiagramElement elementOf(INARY nary) {
        IDiagramElement e = mock(IDiagramElement.class);
        when(e.getModelElement()).thenReturn(nary);
        return e;
    }

    private static INARY nary(IRelationshipEnd... participants) {
        INARY nary = mock(INARY.class);
        when(nary.getName()).thenReturn("nary1");
        when(nary.getParent()).thenReturn(null);
        when(nary.toFromRelationshipEndArray()).thenReturn(participants);
        when(nary.toToRelationshipEndArray()).thenReturn(new IRelationshipEnd[0]);
        return nary;
    }

    private static IRelationshipEnd participant(String alias, String multiplicity) {
        if (alias == null) {
            return null;
        }
        IAssociationEnd end = mock(IAssociationEnd.class);
        when(end.getMultiplicity()).thenReturn(multiplicity);
        IModelElement m = mock(IModelElement.class);
        when(m.getName()).thenReturn(alias);
        when(m.getParent()).thenReturn(null);
        when(end.getModelElement()).thenReturn(m);
        return end;
    }
}
