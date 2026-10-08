package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;

import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;

import org.junit.jupiter.api.Test;

class ViElementFactoryTest {

    private final ViElementFactory factory = new ViElementFactory();

    @Test
    void shouldWrapModelElementAsVpModelElement() {
        IModelElement element = mock(IModelElement.class);
        ViElement result = factory.from(element);
        assertInstanceOf(ViModelElement.class, result);
    }

    @Test
    void shouldWrapDiagramAsVpDiagram() {
        IDiagramUIModel diagram = mock(IDiagramUIModel.class);
        ViElement result = factory.from(diagram);
        assertInstanceOf(ViDiagramAsElement.class, result);
    }
}
