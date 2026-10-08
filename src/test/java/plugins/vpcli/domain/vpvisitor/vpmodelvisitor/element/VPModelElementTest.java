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

    private final VPElementFactory factory = new VPElementFactory();

    @Test
    void shouldReturnNameFromVpElement() {
        IModelElement vp = mock(IModelElement.class);
        when(vp.getName()).thenReturn("Foo");
        VPModelElement e = new VPModelElement(vp, factory);
        assertEquals("Foo", e.getName());
    }

    @Test
    void shouldResolveTypeFromVpModelType() {
        IModelElement vp = mock(IModelElement.class);
        when(vp.getModelType()).thenReturn(ElementType.CLASS.getVPModelType());
        VPModelElement e = new VPModelElement(vp, factory);
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

        VPModelElement e = new VPModelElement(vp, factory);
        List<VPElement> children = e.getChildren(List.of(ElementType.CLASS));

        assertEquals(2, children.size());
        assertTrue(children.get(0) instanceof VPDiagramAsElement);
        assertTrue(children.get(1) instanceof VPModelElement);
        verify(vp).toChildArray(new String[]{ElementType.CLASS.getVPModelType()});
    }

    @Test
    void shouldHandleNullSubDiagramArray() {
        IModelElement vp = mock(IModelElement.class);
        when(vp.toSubDiagramArray()).thenReturn(null);
        when(vp.toChildArray(new String[0])).thenReturn(new IModelElement[0]);

        VPModelElement e = new VPModelElement(vp, factory);
        List<VPElement> children = e.getChildren(List.of());

        assertTrue(children.isEmpty());
    }

    @Test
    void shouldFilterToModelElementKindsOnly() {
        IModelElement vp = mock(IModelElement.class);
        when(vp.toSubDiagramArray()).thenReturn(null);
        when(vp.toChildArray(new String[]{ElementType.CLASS.getVPModelType()}))
            .thenReturn(new IModelElement[0]);

        VPModelElement e = new VPModelElement(vp, factory);
        e.getChildren(List.of(ElementType.CLASS, ElementType.CLASS_DIAGRAM));

        verify(vp).toChildArray(new String[]{ElementType.CLASS.getVPModelType()});
    }
}
