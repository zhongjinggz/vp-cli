package plugins.vpcli.domain.myuml.myelement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MyElementTypeTest {

    @Test
    void shouldResolveModelElementByType() {
        MyElementType t = MyElementType.of(MyElementType.CLASS.getVPModelType());
        assertEquals(MyElementType.CLASS, t);
    }

    @Test
    void shouldResolveDiagramByType() {
        MyElementType t = MyElementType.of(MyElementType.DIAGRAM.getVPModelType());
        assertEquals(MyElementType.DIAGRAM, t);
    }

    @Test
    void shouldResolveAssociationEndByType() {
        MyElementType t = MyElementType.of(MyElementType.PROPERTY.getVPModelType());
        assertEquals(MyElementType.PROPERTY, t);
    }

    @Test
    void shouldReturnUnknownElementWhenNoTypeMatches() {
        assertEquals(MyElementType.UNIMPLEMENTED_ELEMENT, MyElementType.of("no-such-type"));
    }

    @Test
    void shouldReturnUnknownElementWhenKindMismatchOnMatchingType() {
        assertEquals(MyElementType.UNIMPLEMENTED_ELEMENT, MyElementType.of("unknown-type"));
    }

    @Test
    void shouldExposeSuffix() {
        assertEquals(".class", MyElementType.CLASS.getSuffix());
    }

    @Test
    void shouldExposeVpModelType() {
        assertEquals(MyElementType.CLASS.getVPModelType(), MyElementType.CLASS.getVPModelType());
    }

    @Test
    void shouldMatchVpModelType() {
        assertTrue(MyElementType.CLASS.vpModelTypeIs(MyElementType.CLASS.getVPModelType()));
        assertFalse(MyElementType.CLASS.vpModelTypeIs("nothing"));
    }

}
