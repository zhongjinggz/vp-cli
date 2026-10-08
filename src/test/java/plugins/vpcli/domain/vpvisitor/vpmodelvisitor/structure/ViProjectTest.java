package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.structure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.myuml.mycommon.ElementType;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.ViElement;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.ViElementFactory;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitor;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitorFactory;

class ViProjectTest {

    private IProject project;
    private ViProject struct;
    private final List<Call> calls = new ArrayList<>();
    private final ViElementVisitorFactory exportVisitorFactory = pre -> new RecorderElementVisitor(calls, pre);

    private static class Call {
        final String name;
        final boolean last;
        final int depth;

        Call(String name, boolean last, int depth) {
            this.name = name;
            this.last = last;
            this.depth = depth;
        }
    }

    @BeforeEach
    void setUp() {
        project = mock(IProject.class);
        struct = new ViProject(project, new ViElementFactory());
    }

    private static final class RecorderElementVisitor extends ViElementVisitor {
        private final List<Call> calls;
        private final int depth;

        RecorderElementVisitor(List<Call> calls, ViElementVisitor pre) {
            this.calls = calls;
            this.depth = pre instanceof RecorderElementVisitor ? ((RecorderElementVisitor) pre).depth + 1 : 0;
        }

        @Override
        public void visit(ViElement viElement) {
            calls.add(new Call(viElement.getName(), isLast(), depth));
        }
    }

    private IModelElement element(String name, IModelElement... children) {
        IModelElement e = mock(IModelElement.class);
        when(e.getName()).thenReturn(name);
        when(e.getModelType()).thenReturn(ElementType.PACKAGE.getVPModelType());
        when(e.toSubDiagramArray()).thenReturn(null);
        when(e.toChildArray(new String[]{ElementType.PACKAGE.getVPModelType()}))
            .thenReturn(children);
        return e;
    }

    @Test
    void shouldVisitTopLevelAndChildrenWithLastFlag() throws IOException {
        IModelElement leaf = element("leaf");
        IModelElement root = element("root", leaf);
        when(project.toModelElementArray(new String[]{ElementType.PACKAGE.getVPModelType()}))
            .thenReturn(new IModelElement[]{root});

        struct.setElementTypes(List.of(ElementType.PACKAGE));
        struct.accept(this.exportVisitorFactory);

        assertEquals(Integer.valueOf(2), Integer.valueOf(calls.size()));
        Call r = calls.get(0);
        assertEquals("root", r.name);
        assertTrue(r.last);
        assertEquals(0, r.depth);

        Call l = calls.get(1);
        assertEquals("leaf", l.name);
        assertTrue(l.last);
        assertEquals(1, l.depth);
    }

    @Test
    void shouldMarkNonLastWhenMultipleTopLevelElements() throws IOException {
        IModelElement a = element("a");
        IModelElement b = element("b");
        when(project.toModelElementArray(new String[]{ElementType.PACKAGE.getVPModelType()}))
            .thenReturn(new IModelElement[]{a, b});

        struct.setElementTypes(List.of(ElementType.PACKAGE));
        struct.accept(this.exportVisitorFactory);

        assertEquals(2, calls.size());
        assertFalse(calls.get(0).last);
        assertTrue(calls.get(1).last);
    }

    @Test
    void shouldHandleNoTopLevelElements() throws IOException {
        when(project.toModelElementArray(new String[0])).thenReturn(new IModelElement[0]);
        struct.setElementTypes(List.of());
        struct.accept(this.exportVisitorFactory);
        assertTrue(calls.isEmpty());
    }

    @Test
    void shouldFilterNonModelElementTypes() throws IOException {
        when(project.toModelElementArray(new String[0])).thenReturn(new IModelElement[0]);
        struct.setElementTypes(List.of(ElementType.CLASS_DIAGRAM));
        struct.accept(this.exportVisitorFactory);
        assertTrue(calls.isEmpty());
    }
}
