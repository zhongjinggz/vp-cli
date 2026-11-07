package vpcli.plugin.domain.outputmodel.pumldiagram;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import vpcli.plugin.domain.outputmodel.pumldiagram.PumlEdgeEnd;
import vpcli.plugin.domain.outputmodel.pumldiagram.PumlEdgeEnd.ShapeKind;

/**
 * 针对 {@link PumlEdgeEnd} 的值对象访问器与 {@link ShapeKind} 文本映射。
 */
class PumlEdgeEndTest {

    @Test
    void ofStoresAllFields() {
        PumlEdgeEnd end = PumlEdgeEnd.of("role", "1..*", "owner", ShapeKind.ARROW);
        assertEquals("role", end.getName());
        assertEquals("1..*", end.getMultiplicity());
        assertEquals("owner", end.getRole());
        assertEquals(ShapeKind.ARROW, end.getShapeKind());
    }

    @Test
    void shapeKindTextsMapToPlantUmlSymbols() {
        assertEquals("", ShapeKind.NONE.getText());
        assertEquals(">", ShapeKind.ARROW.getText());
        assertEquals("<", ShapeKind.REVERSE_ARROW.getText());
        assertEquals("|>", ShapeKind.TRIANGLE.getText());
        assertEquals("<|", ShapeKind.REVERSE_TRIANGLE.getText());
        assertEquals("o", ShapeKind.DIAMOND.getText());
        assertEquals("*", ShapeKind.SOLID_DIAMOND.getText());
        assertEquals("x", ShapeKind.CROSS.getText());
    }

    @Test
    void nullFieldsAcceptedByOf() {
        PumlEdgeEnd end = PumlEdgeEnd.of(null, null, null, ShapeKind.NONE);
        assertEquals(null, end.getName());
        assertEquals(null, end.getMultiplicity());
        assertEquals(null, end.getRole());
        assertEquals(ShapeKind.NONE, end.getShapeKind());
    }
}
