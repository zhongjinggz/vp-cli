package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;

import java.util.List;

import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.myuml.mycommon.ElementType;

class VPModelElementTest {

    private final ViElementFactory factory = new ViElementFactory();

    @Test
    void shouldReturnNameFromVpElement() {
        IModelElement vp = mock(IModelElement.class);
        when(vp.getName()).thenReturn("Foo");
        ViModelElement e = new ViModelElement(vp, factory);
        assertEquals("Foo", e.getName());
    }

    @Test
    void shouldResolveTypeFromVpModelType() {
        IModelElement vp = mock(IModelElement.class);
        when(vp.getModelType()).thenReturn(ElementType.CLASS.getVPModelType());
        ViModelElement e = new ViModelElement(vp, factory);
        assertEquals(ElementType.CLASS, e.getType());
    }

    @Test
    void shouldCollectSubDiagramsAndChildElements() {
        IModelElement vp = mock(IModelElement.class);
        IDiagramUIModel subDiagram = mock(IDiagramUIModel.class);
        IModelElement child = mock(IModelElement.class);
        when(vp.toSubDiagramArray()).thenReturn(new IDiagramUIModel[]{subDiagram});
        when(vp.toChildArray(new String[]{ElementType.CLASS.getVPModelType()}))
            .thenReturn(new IModelElement[]{child});

        ViModelElement e = new ViModelElement(vp, factory);
        List<ViElement> children = e.getChildren(List.of(ElementType.CLASS));

        assertEquals(2, children.size());
        assertTrue(children.get(0) instanceof ViDiagramAsElement);
        assertTrue(children.get(1) instanceof ViModelElement);
        verify(vp).toChildArray(new String[]{ElementType.CLASS.getVPModelType()});
    }

    @Test
    void shouldHandleNullSubDiagramArray() {
        IModelElement vp = mock(IModelElement.class);
        when(vp.toSubDiagramArray()).thenReturn(null);
        when(vp.toChildArray(new String[0])).thenReturn(new IModelElement[0]);

        ViModelElement e = new ViModelElement(vp, factory);
        List<ViElement> children = e.getChildren(List.of());

        assertTrue(children.isEmpty());
    }

    @Test
    void shouldFilterToModelElementKindsOnly() {
        IModelElement vp = mock(IModelElement.class);
        when(vp.toSubDiagramArray()).thenReturn(null);
        when(vp.toChildArray(new String[]{ElementType.CLASS.getVPModelType()}))
            .thenReturn(new IModelElement[0]);

        ViModelElement e = new ViModelElement(vp, factory);
        e.getChildren(List.of(ElementType.CLASS, ElementType.CLASS_DIAGRAM));

        verify(vp).toChildArray(new String[]{ElementType.CLASS.getVPModelType()});
    }
}
