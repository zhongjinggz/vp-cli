package vpcli.plugin.domain.vpvisitor.vpmodelvisitor.element;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.vp.plugin.diagram.IDiagramUIModel;

import org.junit.jupiter.api.Test;
import vpcli.plugin.domain.vpmodelvisitor.element.fundation.ViElementType;
import vpcli.plugin.domain.vpmodelvisitor.element.vidiagram.ViDiagram;

class MyDiagramFactoryConverterAsElementTest {

    @Test
    void shouldReturnNameFromDiagram() {
        IDiagramUIModel diagram = mock(IDiagramUIModel.class);
        when(diagram.getName()).thenReturn("D");
        ViDiagram vp = new ViDiagram(diagram);
        assertEquals("D", vp.getName());
    }

    @Test
    void shouldResolveTypeFromDiagramType() {
        IDiagramUIModel diagram = mock(IDiagramUIModel.class);
        when(diagram.getType()).thenReturn(ViElementType.DIAGRAM.getVPModelType());
        ViDiagram vp = new ViDiagram(diagram);
        assertEquals(ViElementType.DIAGRAM, vp.getType());
    }

}
