package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;

import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;

import org.junit.jupiter.api.Test;

class VPElementFactoryTest {

    private final VPElementFactory factory = new VPElementFactory();

    @Test
    void shouldWrapModelElementAsVpModelElement() {
        IModelElement element = mock(IModelElement.class);
        VPElement result = factory.from(element);
        assertInstanceOf(VPModelElement.class, result);
    }

    @Test
    void shouldWrapDiagramAsVpDiagram() {
        IDiagramUIModel diagram = mock(IDiagramUIModel.class);
        VPElement result = factory.from(diagram);
        assertInstanceOf(VPDiagramAsElement.class, result);
    }
}
