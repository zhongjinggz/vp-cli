package plugins.vpcli.domain.outputmodel;

public class PumlStatement {
    private static final String indentSpace ="    ";

    private final int indentLevel;
    private final String text;

    public PumlStatement(int indentLevel, String text) {
        this.text = text;
        this.indentLevel = indentLevel;
    }

    @Override
    public String toString() {
        return indentSpace.repeat(indentLevel) + text;
    }

}
