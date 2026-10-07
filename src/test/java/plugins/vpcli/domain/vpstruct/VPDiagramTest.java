package plugins.vpcli.domain.vpstruct;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.vp.plugin.diagram.IDiagramUIModel;

import java.util.List;

import org.junit.jupiter.api.Test;

class VPDiagramTest {

    @Test
    void shouldReturnNameFromDiagram() {
        IDiagramUIModel diagram = mock(IDiagramUIModel.class);
        when(diagram.getName()).thenReturn("D");
        VPDiagram vp = new VPDiagram(diagram);
        assertEquals("D", vp.getName());
    }

    @Test
    void shouldResolveTypeFromDiagramType() {
        IDiagramUIModel diagram = mock(IDiagramUIModel.class);
        when(diagram.getType()).thenReturn(ElementType.CLASS_DIAGRAM.getVPModelType());
        VPDiagram vp = new VPDiagram(diagram);
        assertEquals(ElementType.CLASS_DIAGRAM, vp.getType());
    }

    @Test
    void shouldResolveNullTypeForUnknownDiagramType() {
        IDiagramUIModel diagram = mock(IDiagramUIModel.class);
        when(diagram.getType()).thenReturn("UnknownDiagram");
        VPDiagram vp = new VPDiagram(diagram);
        assertEquals(null, vp.getType());
    }

    @Test
    void shouldReturnEmptyChildren() {
        IDiagramUIModel diagram = mock(IDiagramUIModel.class);
        VPDiagram vp = new VPDiagram(diagram);
        assertTrue(vp.getChildren(List.of(ElementType.ALL_DIAGRAMS)).isEmpty());
    }
}
