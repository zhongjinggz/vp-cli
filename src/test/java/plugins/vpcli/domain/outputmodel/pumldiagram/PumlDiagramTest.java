package plugins.vpcli.domain.outputmodel.pumldiagram;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * 针对 {@link PumlDiagram}：缩进层级、beginBlock/endBlock 组合、正文写入与
 * {@code toString()} 的 @startuml/@enduml 骨架。
 */
class PumlDiagramTest {

    @Test
    void nameIsCarriedThrough() {
        assertEquals("root", new PumlDiagram("root").getName());
    }

    @Test
    void emptyDiagramRendersSkeletonWithNoStatements() {
        PumlDiagram diagram = new PumlDiagram("d");
        assertEquals("@startuml\nallowmixing\n\n@enduml\n", diagram.toString());
    }

    @Test
    void singleStatementAppearsRaw() {
        PumlDiagram diagram = new PumlDiagram("d");
        diagram.writeText("class A");
        assertTrue(diagram.toString().contains("class A"));
    }

    @Test
    void blockIndentsNestedStatementsByFourSpaces() {
        PumlDiagram diagram = new PumlDiagram("d");
        diagram.beginBlock("package p");
        diagram.writeText("inner");
        diagram.endBlock();
        assertEquals(
            "@startuml\nallowmixing\n\npackage p {\n    inner\n}\n\n@enduml\n",
            diagram.toString());
    }

    @Test
    void multipleNestingLevelsAccumulateIndentation() {
        PumlDiagram diagram = new PumlDiagram("d");
        diagram.beginBlock("package outer");
        diagram.beginBlock("package inner");
        diagram.writeText("leaf");
        diagram.endBlock();
        diagram.endBlock();
        assertTrue(diagram.toString().contains("        leaf"),
            () -> "实际输出：\n" + diagram);
    }

    @Test
    void interleavedIndentOutdentControlExplicitly() {
        PumlDiagram diagram = new PumlDiagram("d");
        diagram.indent();
        diagram.writeText("x");
        diagram.outdent();
        diagram.writeText("y");
        assertTrue(diagram.toString().contains("    x\n")
                && diagram.toString().contains("\ny\n"),
            () -> "实际输出：\n" + diagram);
    }
}
