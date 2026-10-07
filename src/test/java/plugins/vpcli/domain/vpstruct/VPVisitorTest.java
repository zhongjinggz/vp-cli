package plugins.vpcli.domain.vpstruct;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class VPVisitorTest {

    private static final class NoOpVisitor extends VPVisitor {
        @Override
        public void visit(VPStructElement vpStructElement) {
        }
    }

    @Test
    void shouldBeNonLastByDefault() {
        assertFalse(new NoOpVisitor().isLast());
    }

    @Test
    void shouldReflectSetLastTrue() {
        NoOpVisitor visitor = new NoOpVisitor();
        visitor.setLast(true);
        assertTrue(visitor.isLast());
    }

    @Test
    void shouldToggleLastBackToFalse() {
        NoOpVisitor visitor = new NoOpVisitor();
        visitor.setLast(true);
        visitor.setLast(false);
        assertFalse(visitor.isLast());
    }
}
