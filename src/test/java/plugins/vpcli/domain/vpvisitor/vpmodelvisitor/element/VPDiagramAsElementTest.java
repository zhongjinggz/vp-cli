package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.vp.plugin.diagram.IDiagramUIModel;

import java.util.List;

import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.myuml.mycommon.ElementType;

class VPDiagramAsElementTest {

    @Test
    void shouldReturnNameFromDiagram() {
        IDiagramUIModel diagram = mock(IDiagramUIModel.class);
        when(diagram.getName()).thenReturn("D");
        VPDiagramAsElement vp = new VPDiagramAsElement(diagram);
        assertEquals("D", vp.getName());
    }

    @Test
    void shouldResolveTypeFromDiagramType() {
        IDiagramUIModel diagram = mock(IDiagramUIModel.class);
        when(diagram.getType()).thenReturn(ElementType.CLASS_DIAGRAM.getVPModelType());
        VPDiagramAsElement vp = new VPDiagramAsElement(diagram);
        assertEquals(ElementType.CLASS_DIAGRAM, vp.getType());
    }

    @Test
    void shouldResolveNullTypeForUnknownDiagramType() {
        IDiagramUIModel diagram = mock(IDiagramUIModel.class);
        when(diagram.getType()).thenReturn("UnknownDiagram");
        VPDiagramAsElement vp = new VPDiagramAsElement(diagram);
        assertEquals(null, vp.getType());
    }

    @Test
    void shouldReturnEmptyChildren() {
        IDiagramUIModel diagram = mock(IDiagramUIModel.class);
        VPDiagramAsElement vp = new VPDiagramAsElement(diagram);
        assertTrue(vp.getChildren(List.of(ElementType.ALL_DIAGRAMS)).isEmpty());
    }
}
