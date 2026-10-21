package plugins.vpcli.domain.myuml.mydiagram.mydiagramelement;

import java.util.Objects;

import static com.vp.plugin.diagram.IShapeTypeConstants.*;

public enum DiagramElementType {
    DIAGRAM("", Kind.DIAGRAM),
    PACKAGE(SHAPE_TYPE_PACKAGE, Kind.SHAPE),
    MODEL(SHAPE_TYPE_MODEL, Kind.SHAPE),
    CLASS(SHAPE_TYPE_CLASS, Kind.SHAPE),
    USECASE(SHAPE_TYPE_USE_CASE, Kind.SHAPE),
    ASSOCIATION(SHAPE_TYPE_ASSOCIATION, Kind.SHAPE);

    private final String vpShapeType;
    private final Kind kind;

    DiagramElementType(String vpShapeType, Kind kind) {
        this.vpShapeType = vpShapeType;
        this.kind = kind;
    }

    public static DiagramElementType of(String vpShapeType) {
        for (var aValue : values()) {
            if (aValue.vpShapeTypeIs(vpShapeType)) {
                return aValue;
            }
        }
        throw new IllegalArgumentException("'" + vpShapeType + "' is not a valid shape type.");
    }


    public boolean vpShapeTypeIs(String theType) {
        return Objects.equals(this.vpShapeType, theType);
    }

    public boolean isShape() {
        return Objects.equals(this.kind, Kind.SHAPE);
    }

    public boolean isEdge(){
        return Objects.equals(this.kind, Kind.EDGE);
    }

    public static enum Kind {
        DIAGRAM,
        SHAPE,
        EDGE
    }

}
