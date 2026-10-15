package plugins.vpcli.domain.outputmodel;

import plugins.vpcli.domain.myuml.mydiagram.DiagramType;

import java.util.ArrayList;
import java.util.List;

public class PumlDiagram {
    private String name;
    private DiagramType diagramType;
    private int indentLevel = 0;
    private List<PumlStatement> statements = new ArrayList<>();

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

    public void beginBlock(String name) {

        addStatement(name + " {");
        indent();
    }

    public void endBlock() {
        outdent();
        addStatement("}\n");

    }

    public void writeTerminalElement(String elementText) {
        addStatement (elementText);
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
