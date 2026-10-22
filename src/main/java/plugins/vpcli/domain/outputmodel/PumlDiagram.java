package plugins.vpcli.domain.outputmodel;

import org.jetbrains.annotations.NotNull;
import plugins.vpcli.domain.myuml.mydiagram.DiagramType;

import java.util.ArrayList;
import java.util.List;

public class PumlDiagram {
    private final String name;
    private final DiagramType diagramType;
    private int indentLevel = 0;
    private final List<PumlStatement> statements = new ArrayList<>();

    public PumlDiagram(String name, DiagramType diagramType) {
        this.name = name;
        this.diagramType = diagramType;
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

    public void writeEdge(PumlEdgeEnd from, PumlEdgeEnd to, LineStyle lineStyle) {
        // 语法为：fromName "fromMult" fromShape--toShape "toMult" toName，shape 紧贴 --。
        String fromName = from.getName();
        if (from.getMultiplicity() != null && !from.getMultiplicity().isEmpty()) {
            fromName += " \"" + from.getMultiplicity() + "\"";
        }

        String toName = to.getName();
        if (to.getMultiplicity() != null && !to.getMultiplicity().isEmpty()) {
            toName = "\"" + to.getMultiplicity() + "\" " + toName;
        }

        String line = from.getShapeKind().getText() + lineStyle.getText() + to.getShapeKind().getText();
        addStatement(fromName + " " + line + " " + toName);
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
