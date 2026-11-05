package plugins.vpcli.domain.outputmodel.pumldiagram;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PumlDiagram {
    private final String name;
    private int indentLevel = 0;
    private final List<PumlStatement> statements = new ArrayList<>();

    public PumlDiagram(String name) {
        this.name = name;
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
        if (!blockSuffix.isEmpty()) {
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

    public void writeText(String elementText) {
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

}
