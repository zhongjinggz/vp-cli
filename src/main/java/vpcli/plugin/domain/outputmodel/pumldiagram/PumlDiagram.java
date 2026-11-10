package vpcli.plugin.domain.outputmodel.pumldiagram;

//import org.jetbrains.annotations.NotNull;

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

    public void beginBlock(String header) {
        addStatement(header + " {");
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
        StringBuilder result = new StringBuilder("@startuml\nallowmixing\n\n");

        for (PumlStatement statement : this.statements) {
            result.append(statement.toString()).append("\n");
        }

        result.append("@enduml\n");

        return result.toString();
    }

}
