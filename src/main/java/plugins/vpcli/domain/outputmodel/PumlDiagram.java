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

    public void beginBlock(String name) {

        addStatement(name + " {");
        indent();
    }

    public void endBlock() {
        outdent();
        addStatement("}");
    }

    public void writeTerminalElement(String elementText) {
        addStatement (elementText);
    }

    private void addStatement(String text) {
        this.statements.add(new PumlStatement(this.indentLevel, text));
    }
}
