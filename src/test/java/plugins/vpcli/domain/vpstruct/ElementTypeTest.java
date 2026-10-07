package plugins.vpcli.domain.vpstruct;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static plugins.vpcli.domain.vpstruct.ElementType.Kind.DIAGRAM;
import static plugins.vpcli.domain.vpstruct.ElementType.Kind.MODEL_ELEMENT;

import org.junit.jupiter.api.Test;

class ElementTypeTest {

    @Test
    void shouldResolveModelElementByType() {
        ElementType t = ElementType.of(MODEL_ELEMENT, ElementType.CLASS.getVPModelType());
        assertEquals(ElementType.CLASS, t);
    }

    @Test
    void shouldResolveDiagramByType() {
        ElementType t = ElementType.of(DIAGRAM, ElementType.CLASS_DIAGRAM.getVPModelType());
        assertEquals(ElementType.CLASS_DIAGRAM, t);
    }

    @Test
    void shouldReturnNullWhenNoTypeMatches() {
        assertNull(ElementType.of(MODEL_ELEMENT, "no-such-type"));
    }

    @Test
    void shouldReturnNullWhenKindMismatchOnMatchingType() {
        assertNull(ElementType.of(DIAGRAM, ElementType.CLASS.getVPModelType()));
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

    @Test
    void shouldMatchKind() {
        assertTrue(ElementType.CLASS.kindIs(MODEL_ELEMENT));
        assertFalse(ElementType.CLASS.kindIs(DIAGRAM));
    }
}
