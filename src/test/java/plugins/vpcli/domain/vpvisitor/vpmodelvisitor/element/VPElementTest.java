package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.myuml.mycommon.ElementType;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.VPElementVisitor;

class VPElementTest {

    private static final class StubElement extends VPElement {
        @Override
        public String getName() {
            return "stub";
        }

        @Override
        public ElementType getType() {
            return null;
        }

        @Override
        public List<VPElement> getChildren(List<ElementType> elementTypes) {
            return List.of();
        }
    }

    private static final class CapturingElementVisitor extends VPElementVisitor {
        VPElement visited;

        @Override
        public void visit(VPElement vpElement) {
            this.visited = vpElement;
        }
    }

    @Test
    void shouldAcceptDelegatesToVisitor() throws IOException {
        StubElement element = new StubElement();
        CapturingElementVisitor visitor = new CapturingElementVisitor();
        element.accept(visitor);
        assertTrue(visitor.visited == element);
    }

    @Test
    void stubReturnsExpectedDefaults() {
        StubElement element = new StubElement();
        assertNull(element.getType());
        assertTrue(element.getChildren(List.of()).isEmpty());
    }
}
