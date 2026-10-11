package plugins.vpcli.domain.myuml.mydiagram;

import java.util.Objects;

import static com.vp.plugin.diagram.IShapeTypeConstants.*;

public enum DiagramElementType {
    DIAGRAM(""),
    PACKAGE(SHAPE_TYPE_PACKAGE),
    CLASS(SHAPE_TYPE_CLASS),
    USECASE(SHAPE_TYPE_USE_CASE);

    private final String vpShapeType;

    DiagramElementType(String vpShapeType) {
        this.vpShapeType = vpShapeType;
    }

    public static DiagramElementType of(String vpShapeType) {
        for (var aValue : values()) {
            if (aValue.vpShapeTypeIs(vpShapeType)) {
                return aValue;
            }
        }
        return null;
    }


    public boolean vpShapeTypeIs(String theType) {
        return Objects.equals(this.vpShapeType, theType);
    }
}
