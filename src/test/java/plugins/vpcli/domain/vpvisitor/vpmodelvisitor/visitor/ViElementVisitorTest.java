package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.ViElement;

class ViElementVisitorTest {

    private static final class NoOpElementVisitor extends ViElementVisitor {
        @Override
        public void visit(ViElement viElement) {
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
