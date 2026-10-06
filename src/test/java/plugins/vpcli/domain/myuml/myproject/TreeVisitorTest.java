package plugins.vpcli.domain.myuml.myproject;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import plugins.vpcli.domain.vpstruct.ElementType;
import plugins.vpcli.domain.vpstruct.VPStructElement;

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

    private VPStructElement element(String name, ElementType type) {
        return new VPStructElement() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public ElementType getType() {
                return type;
            }

            @Override
            public List<VPStructElement> getChildren(List<ElementType> elementTypes) {
                return List.of();
            }
        };
    }

    @Test
    void shouldUseEmptyPrefixWhenParentIsNull() throws Throwable {
        TreeVisitor visitor = new TreeVisitor(null);
        visitor.setLast(true);
        String out = print(() -> visitor.visit(element("A", ElementType.CLASS)));
        assertEquals("└── A.class\n", out);
    }

    @Test
    void shouldAppendVerticalBarWhenParentIsNotLast() throws Throwable {
        TreeVisitor parent = new TreeVisitor(null);
        TreeVisitor child = new TreeVisitor(parent);
        child.setLast(true);
        String out = print(() -> child.visit(element("A", ElementType.CLASS)));
        assertEquals("│   └── A.class\n", out);
    }

    @Test
    void shouldAppendSpacesWhenParentIsLast() throws Throwable {
        TreeVisitor parent = new TreeVisitor(null);
        parent.setLast(true);
        TreeVisitor child = new TreeVisitor(parent);
        child.setLast(true);
        String out = print(() -> child.visit(element("A", ElementType.CLASS)));
        assertEquals("    └── A.class\n", out);
    }

    @Test
    void shouldRenderNonLastChildWithAllBars() throws Throwable {
        TreeVisitor parent = new TreeVisitor(null);
        TreeVisitor child = new TreeVisitor(parent);
        child.setLast(false);
        String out = print(() -> child.visit(element("A", ElementType.CLASS)));
        assertEquals("│   ├── A.class\n", out);
    }

    @Test
    void shouldAccumulatePrefixAcrossMultipleLevels() throws Throwable {
        TreeVisitor root = new TreeVisitor(null);
        TreeVisitor middle = new TreeVisitor(root);
        TreeVisitor leaf = new TreeVisitor(middle);
        leaf.setLast(true);
        String out = print(() -> leaf.visit(element("A", ElementType.CLASS)));
        assertEquals("│   │   └── A.class\n", out);
    }
}
