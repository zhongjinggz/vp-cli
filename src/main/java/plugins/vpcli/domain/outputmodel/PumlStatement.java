package plugins.vpcli.domain.outputmodel;

public class PumlStatement {
    private int indentLevel = 0;
    private String text;

    public PumlStatement(int indentLevel, String text) {
        this.text = text;
        this.indentLevel = indentLevel;
    }

    public int getIndentLevel() {
        return indentLevel;
    }
    public String getText() {
        return text;
    }

}
