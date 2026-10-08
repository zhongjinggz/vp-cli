package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.vp.plugin.diagram.IDiagramUIModel;

import java.util.List;

import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.myuml.mycommon.ElementType;

class ViDiagramAsElementTest {

    @Test
    void shouldReturnNameFromDiagram() {
        IDiagramUIModel diagram = mock(IDiagramUIModel.class);
        when(diagram.getName()).thenReturn("D");
        ViDiagramAsElement vp = new ViDiagramAsElement(diagram);
        assertEquals("D", vp.getName());
    }

    @Test
    void shouldResolveTypeFromDiagramType() {
        IDiagramUIModel diagram = mock(IDiagramUIModel.class);
        when(diagram.getType()).thenReturn(ElementType.CLASS_DIAGRAM.getVPModelType());
        ViDiagramAsElement vp = new ViDiagramAsElement(diagram);
        assertEquals(ElementType.CLASS_DIAGRAM, vp.getType());
    }

    @Test
    void shouldResolveNullTypeForUnknownDiagramType() {
        IDiagramUIModel diagram = mock(IDiagramUIModel.class);
        when(diagram.getType()).thenReturn("UnknownDiagram");
        ViDiagramAsElement vp = new ViDiagramAsElement(diagram);
        assertEquals(null, vp.getType());
    }

    @Test
    void shouldReturnEmptyChildren() {
        IDiagramUIModel diagram = mock(IDiagramUIModel.class);
        ViDiagramAsElement vp = new ViDiagramAsElement(diagram);
        assertTrue(vp.getChildren(List.of(ElementType.DIAGRAM)).isEmpty());
    }
}
