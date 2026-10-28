package plugins.vpcli.domain.outputmodel.pumelementexporter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.model.IAnchor;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.INOTE;
import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.outputmodel.PumlDiagram;

/**
 * 步 #19：注释（Note）与注释–元素连线（Anchor）的导出渲染。
 */
class PumlNoteAnchorExporterTest {

    // ---- 注释 Note ----

    @Test
    void namedNoteRendersNameAlias() {
        PumlDiagram diagram = new PumlDiagram("d");
        INOTE note = mock(INOTE.class);
        when(note.getName()).thenReturn("N1");
        when(note.getId()).thenReturn("note-id");
        when(note.getDescription()).thenReturn("UML Style");

        new PumlNoteExporter().export(diagram, elem(note));

        assertTrue(diagram.toString().contains("note \"UML Style\" as N1"),
            () -> "实际输出：\n" + diagram);
    }

    @Test
    void unnamedNoteUsesIdAsAlias() {
        PumlDiagram diagram = new PumlDiagram("d");
        INOTE note = mock(INOTE.class);
        when(note.getName()).thenReturn("");
        when(note.getId()).thenReturn("note-1");
        when(note.getDescription()).thenReturn("text");

        new PumlNoteExporter().export(diagram, elem(note));

        assertTrue(diagram.toString().contains("note \"text\" as note_1"),
            () -> "实际输出：\n" + diagram);
    }

    @Test
    void noteEscapesNewlinesInBody() {
        PumlDiagram diagram = new PumlDiagram("d");
        INOTE note = mock(INOTE.class);
        when(note.getName()).thenReturn("N2");
        when(note.getId()).thenReturn("note-id");
        when(note.getDescription()).thenReturn("line1\nline2");

        new PumlNoteExporter().export(diagram, elem(note));

        assertTrue(diagram.toString().contains("note \"line1\\nline2\" as N2"),
            () -> "实际输出：\n" + diagram);
    }

    @Test
    void emptyNoteDescriptionIsSkipped() {
        PumlDiagram diagram = new PumlDiagram("d");
        INOTE note = mock(INOTE.class);
        when(note.getName()).thenReturn("N3");
        when(note.getId()).thenReturn("note-id");
        when(note.getDescription()).thenReturn("");

        new PumlNoteExporter().export(diagram, elem(note));

        assertTrue(!diagram.toString().contains("note "),
            () -> "空正文注释不应输出。实际输出：\n" + diagram);
    }

    // ---- 注释连线 Anchor ----

    @Test
    void anchorRendersSimpleDashedEdge() {
        PumlDiagram diagram = new PumlDiagram("d");
        IModelElement from = model("class2");
        IModelElement to = model("N1");
        IAnchor anchor = mock(IAnchor.class);
        when(anchor.getFrom()).thenReturn(from);
        when(anchor.getTo()).thenReturn(to);

        new PumlAnchorExporter().export(diagram, elem(anchor));

        String out = diagram.toString();
        assertTrue(out.contains("class2 .. N1"),
            () -> "实际输出：\n" + out);
        assertTrue(!out.contains(": "),
            () -> "anchor 不应有冒号标签。实际输出：\n" + out);
    }

    // ---- helpers ----

    private static IModelElement model(String name) {
        IModelElement m = mock(IModelElement.class);
        when(m.getName()).thenReturn(name);
        return m;
    }

    private static IDiagramElement elem(IModelElement modelElement) {
        IDiagramElement e = mock(IDiagramElement.class);
        when(e.getModelElement()).thenReturn(modelElement);
        return e;
    }
}
