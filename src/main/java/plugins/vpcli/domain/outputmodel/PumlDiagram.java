package plugins.vpcli.domain.outputmodel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PumlDiagram {
    private final String name;
//    private final DiagramType diagramType;
    private int indentLevel = 0;
    private final List<PumlStatement> statements = new ArrayList<>();

    public PumlDiagram(String name) {
        this.name = name;
//        this.diagramType = diagramType;
    }

    public void indent() {
        indentLevel++;
    }

    public void outdent() {
        indentLevel--;
    }

    public void beginBlock(
        @NotNull String blockType,
        @NotNull String name,
        @NotNull String pumlAlias,
        @NotNull String blockSuffix
    ) {
        String text = blockType + " \"" + name + "\" as " + pumlAlias;
        if (blockSuffix.length() > 0) {
            text += " " + blockSuffix;
        }
        text += " {";

        addStatement(text);
        indent();

    }

    public void endBlock() {
        outdent();
        addStatement("}\n");

    }

    public void writeTerminalShape(String elementText) {
        addStatement(elementText);
    }

    private void addStatement(String text) {
        this.statements.add(new PumlStatement(this.indentLevel, text));
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("@startuml\nallowmixing\n\n");

        for (PumlStatement statement : this.statements) {
            sb.append(statement.toString()).append("\n");
        }

        sb.append("@enduml\n");

        return sb.toString();
    }

    public void writeEdge(String fromName, String toName) {
        var text = fromName + " -- " + toName;
        addStatement(text);

    }

    /**
     * 画一条边。from 恒在左、to 恒在右。
     * name 为空/空白时不输出 `: name` 也不输出阅读方向三角；
     * name 非空时按 direction 在名称旁渲染阅读方向小三角：FROM_TO→`name >`、TO_FROM→`< name`、NONE→仅名称无三角。
     */
    public void writeEdge(PumlEdgeEnd from, PumlEdgeEnd to, LineStyle lineStyle, String name, NameDirection direction) {
        // 每一端只能有一个引号标签（role+multiplicity 合并），shape 紧贴 --。
        String fromName = from.getName();
        String fromLabel = edgeLabel(from.getRole(), from.getMultiplicity());
        if (!fromLabel.isEmpty()) {
            fromName += " \"" + fromLabel + "\"";
        }

        String toName = to.getName();
        String toLabel = edgeLabel(to.getRole(), to.getMultiplicity());
        if (!toLabel.isEmpty()) {
            toName = "\"" + toLabel + "\" " + toName;
        }

        String line = from.getShapeKind().getText() + lineStyle.getText() + to.getShapeKind().getText();
        String base = fromName + " " + line + " " + toName;

        // 名称及阅读方向三角：名称非空才追加；三角由阅读方向派生（FROM_TO 在右、TO_FROM 在左、NONE 无）
        if (name != null && !name.isBlank()) {
            addStatement(base + ": " + namedLabel(name, direction));
        } else {
            addStatement(base);
        }
    }

    /** 按阅读方向为关联名称加上小三角：FROM_TO→`名称>`、TO_FROM→`<名称`、NONE→仅名称。 */
    private String namedLabel(String name, NameDirection direction) {
        switch (direction) {
            case FROM_TO:
                return name + " >";
            case TO_FROM:
                return "< " + name;
            default:
                return name;
        }
    }

    /** 合并 role 与 multiplicity 为单个引号标签，空的部分省略。 */
    private String edgeLabel(String role, String multiplicity) {
        StringBuilder sb = new StringBuilder();
        if (role != null && !role.isEmpty()) {
            sb.append(role);
        }
        if (multiplicity != null && !multiplicity.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(multiplicity);
        }
        return sb.toString();
    }

    public enum LineStyle {
        SOLID("--"),
        DASHED("..");

        private final String text;

        LineStyle(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }
}
