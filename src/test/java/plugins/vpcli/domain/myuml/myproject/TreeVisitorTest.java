package plugins.vpcli.domain.myuml.myproject;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import plugins.vpcli.domain.myuml.mycommon.ElementType;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.ViElement;

class TreeVisitorTest {

    private String print(Executable executable) throws Throwable {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        try (PrintStream ps = new PrintStream(buffer, true, StandardCharsets.UTF_8)) {
            System.setOut(ps);
            executable.execute();
        } finally {
            System.setOut(originalOut);
        }
        return buffer.toString();
    }

    private ViElement element(String name, ElementType type) {
        return new ViElement() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public ElementType getType() {
                return type;
            }

            @Override
            public List<ViElement> getChildren(List<ElementType> elementTypes) {
                return List.of();
            }
        };
    }

    @Test
    void shouldUseEmptyPrefixWhenParentIsNull() throws Throwable {
        TreeElementVisitor visitor = new TreeElementVisitor(null);
        visitor.setLast(true);
        String out = print(() -> visitor.visit(element("A", ElementType.CLASS)));
        assertEquals("└── A.class\n", out);
    }

    @Test
    void shouldAppendVerticalBarWhenParentIsNotLast() throws Throwable {
        TreeElementVisitor parent = new TreeElementVisitor(null);
        TreeElementVisitor child = new TreeElementVisitor(parent);
        child.setLast(true);
        String out = print(() -> child.visit(element("A", ElementType.CLASS)));
        assertEquals("│   └── A.class\n", out);
    }

    @Test
    void shouldAppendSpacesWhenParentIsLast() throws Throwable {
        TreeElementVisitor parent = new TreeElementVisitor(null);
        parent.setLast(true);
        TreeElementVisitor child = new TreeElementVisitor(parent);
        child.setLast(true);
        String out = print(() -> child.visit(element("A", ElementType.CLASS)));
        assertEquals("    └── A.class\n", out);
    }

    @Test
    void shouldRenderNonLastChildWithAllBars() throws Throwable {
        TreeElementVisitor parent = new TreeElementVisitor(null);
        TreeElementVisitor child = new TreeElementVisitor(parent);
        child.setLast(false);
        String out = print(() -> child.visit(element("A", ElementType.CLASS)));
        assertEquals("│   ├── A.class\n", out);
    }

    @Test
    void shouldAccumulatePrefixAcrossMultipleLevels() throws Throwable {
        TreeElementVisitor root = new TreeElementVisitor(null);
        TreeElementVisitor middle = new TreeElementVisitor(root);
        TreeElementVisitor leaf = new TreeElementVisitor(middle);
        leaf.setLast(true);
        String out = print(() -> leaf.visit(element("A", ElementType.CLASS)));
        assertEquals("│   │   └── A.class\n", out);
    }
}
