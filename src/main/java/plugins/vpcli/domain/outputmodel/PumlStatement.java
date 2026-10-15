package plugins.vpcli.domain.outputmodel;

public class PumlStatement {
    private static String indentSpace ="    ";

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

    @Override
    public String toString() {
        return indentSpace.repeat(indentLevel) + text;
    }

}
