package plugins.vpcli.domain.outputmodel.pumelementexporter;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.model.INOTE;
import jakarta.inject.Inject;

import jakarta.inject.Singleton;
import plugins.vpcli.domain.outputmodel.PumlDiagram;

@Singleton
public class PumlNoteExporter extends PumlElementExporter {
    @Inject
    public PumlNoteExporter() {
    }

    @Override
    public String getShapeType() {
        return IShapeTypeConstants.SHAPE_TYPE_NOTE;
    }

    /**
     * 步 #19：渲染注释块 `note "<正文>" as <别名>`。
     * 别名规则沿用 getPumlAlias：无名时以 ID 代替 Name（与 Anchor 连线引用一致）。
     * 正文为空时跳过输出（与旧版行为一致）。
     */
    @Override
    public void export(PumlDiagram result, IDiagramElement vpDiagramElement) {
        INOTE note = (INOTE) vpDiagramElement.getModelElement();

        String description = note.getDescription();
        if (description == null || description.isEmpty()) {
            return;
        }

        String body = description.replace("\n", "\\n");
        String alias = getPumlAlias(note);

        result.writeTerminalShape("note \"" + body + "\" as " + alias);
    }
}
