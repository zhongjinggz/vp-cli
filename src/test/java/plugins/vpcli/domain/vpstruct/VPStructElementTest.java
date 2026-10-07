package plugins.vpcli.domain.vpstruct;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class VPStructElementTest {

    private static final class StubElement extends VPStructElement {
        @Override
        public String getName() {
            return "stub";
        }

        @Override
        public ElementType getType() {
            return null;
        }

        @Override
        public List<VPStructElement> getChildren(List<ElementType> elementTypes) {
            return List.of();
        }
    }

    private static final class CapturingVisitor extends VPVisitor {
        VPStructElement visited;

        @Override
        public void visit(VPStructElement vpStructElement) {
            this.visited = vpStructElement;
        }
    }

    @Test
    void shouldAcceptDelegatesToVisitor() {
        StubElement element = new StubElement();
        CapturingVisitor visitor = new CapturingVisitor();
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
