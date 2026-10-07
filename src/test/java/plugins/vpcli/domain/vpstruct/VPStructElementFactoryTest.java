package plugins.vpcli.domain.vpstruct;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;

import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;

import org.junit.jupiter.api.Test;

class VPStructElementFactoryTest {

    private final VPStructElementFactory factory = new VPStructElementFactory();

    @Test
    void shouldWrapModelElementAsVpModelElement() {
        IModelElement element = mock(IModelElement.class);
        VPStructElement result = factory.from(element);
        assertInstanceOf(VPModelElement.class, result);
    }

    @Test
    void shouldWrapDiagramAsVpDiagram() {
        IDiagramUIModel diagram = mock(IDiagramUIModel.class);
        VPStructElement result = factory.from(diagram);
        assertInstanceOf(VPDiagram.class, result);
    }
}
