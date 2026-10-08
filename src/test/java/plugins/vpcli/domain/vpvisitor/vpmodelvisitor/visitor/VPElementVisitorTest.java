package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.VPElement;

class VPElementVisitorTest {

    private static final class NoOpElementVisitor extends VPElementVisitor {
        @Override
        public void visit(VPElement vpElement) {
        }
    }

    @Test
    void shouldBeNonLastByDefault() {
        assertFalse(new NoOpElementVisitor().isLast());
    }

    @Test
    void shouldReflectSetLastTrue() {
        NoOpElementVisitor visitor = new NoOpElementVisitor();
        visitor.setLast(true);
        assertTrue(visitor.isLast());
    }

    @Test
    void shouldToggleLastBackToFalse() {
        NoOpElementVisitor visitor = new NoOpElementVisitor();
        visitor.setLast(true);
        visitor.setLast(false);
        assertFalse(visitor.isLast());
    }
}
