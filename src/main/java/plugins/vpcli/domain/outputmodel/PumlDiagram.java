package plugins.vpcli.domain.outputmodel;

import plugins.vpcli.domain.myuml.mydiagram.DiagramType;

import java.util.List;

public class PumlDiagram {
    private String name;
    private DiagramType diagramType;
    private int indentLevel = 0;
    private List<PumlStatement> statements;

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

    public void beginPackage(String name, List<String> stereotypes) {
        StringBuilder text = new StringBuilder("namespace \"" + name + "\"");
        for (var aStereotype : stereotypes) {
            text.append(" <<").append(aStereotype).append(">> ");
        }
        text.append("{");

        addStatement(indentLevel, text.toString());
        indent();
    }

    public void end() {
        outdent();
        addStatement(indentLevel, "}");
    }

    private void addStatement(int indentLevel, String text) {
        this.statements.add(new PumlStatement(indentLevel, text));
    }

}
