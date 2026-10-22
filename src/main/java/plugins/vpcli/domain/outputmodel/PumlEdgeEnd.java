package plugins.vpcli.domain.outputmodel;

import org.jetbrains.annotations.NotNull;

public class PumlEdgeEnd {
    private final String name;
    private final String multiplicity;
    private final String role;
    private final ShapeKind shapeKind;

    PumlEdgeEnd(
        @NotNull String name,
        @NotNull String multiplicity,
        @NotNull String role,
        @NotNull ShapeKind shapeKind
    ) {
        this.name = name;
        this.multiplicity = multiplicity;
        this.role = role;
        this.shapeKind = shapeKind;
    }

    public static PumlEdgeEnd of(
        String name,
        String multiplicity,
        String role,
        ShapeKind shapeKind
    ) {
        return new PumlEdgeEnd(name, multiplicity, role, shapeKind);
    }

    public String getName() {
        return name;
    }

    public String getMultiplicity() {
        return multiplicity;
    }

    public String getRole() {
        return role;
    }

    public ShapeKind getShapeKind() {
        return shapeKind;
    }

    public enum ShapeKind {
        NONE(""),
        ARROW(">"),
        REVERSE_ARROW("<"),
        TRIANGLE("|>"),
        REVERSE_TRIANGLE("<|"),
        DIAMOND("o"),
        SOLID_DIAMOND("*"),
        CROSS("x");

        private final String text;

        ShapeKind(String text) {
            this.text = text;
        }

        public String getText() {
            return this.text;
        }
    }
}
