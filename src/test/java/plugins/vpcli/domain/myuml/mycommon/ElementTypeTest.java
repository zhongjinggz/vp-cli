package plugins.vpcli.domain.myuml.mycommon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ElementTypeTest {

    @Test
    void shouldResolveModelElementByType() {
        ElementType t = ElementType.of(ElementType.CLASS.getVPModelType());
        assertEquals(ElementType.CLASS, t);
    }

    @Test
    void shouldResolveDiagramByType() {
        ElementType t = ElementType.of(ElementType.DIAGRAM.getVPModelType());
        assertEquals(ElementType.DIAGRAM, t);
    }

    @Test
    void shouldReturnNullWhenNoTypeMatches() {
        assertNull(ElementType.of("no-such-type"));
    }

    @Test
    void shouldReturnNullWhenKindMismatchOnMatchingType() {
        assertNull(ElementType.of("unknown-type"));
    }

    @Test
    void shouldExposeSuffix() {
        assertEquals(".class", ElementType.CLASS.getSuffix());
    }

    @Test
    void shouldExposeVpModelType() {
        assertEquals(ElementType.CLASS.getVPModelType(), ElementType.CLASS.getVPModelType());
    }

    @Test
    void shouldMatchVpModelType() {
        assertTrue(ElementType.CLASS.vpModelTypeIs(ElementType.CLASS.getVPModelType()));
        assertFalse(ElementType.CLASS.vpModelTypeIs("nothing"));
    }

}
