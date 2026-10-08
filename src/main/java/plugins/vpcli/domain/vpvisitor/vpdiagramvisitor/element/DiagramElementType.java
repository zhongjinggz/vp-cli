package plugins.vpcli.domain.vpvisitor.vpdiagramvisitor.element;

import plugins.vpcli.domain.myuml.mycommon.ElementType;

import static com.vp.plugin.diagram.IShapeTypeConstants.*;

public enum DiagramElementType {
    CLASS(SHAPE_TYPE_CLASS),
    PACKAGE(SHAPE_TYPE_PACKAGE ),
    ASSOCIATION(SHAPE_TYPE_ASSOCIATION);

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
        return this.vpShapeType.equals(theType);
    }
}
