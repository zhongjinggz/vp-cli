package plugins.vpcli.domain.myuml.mycommon;

import static com.vp.plugin.model.factory.IModelElementFactory.*;
import static plugins.vpcli.domain.myuml.mycommon.ElementType.Kind.*;

public enum ElementType {
    PACKAGE(MODEL_ELEMENT, MODEL_TYPE_PACKAGE, ".package"),
    MODEL(MODEL_ELEMENT, MODEL_TYPE_MODEL, ".model"),
    CLASS(MODEL_ELEMENT, MODEL_TYPE_CLASS, ".class"),
    USECASE(MODEL_ELEMENT, MODEL_TYPE_USE_CASE, ".usecase"),
    ALL_DIAGRAMS(DIAGRAM, "",""),
    CLASS_DIAGRAM(DIAGRAM, "ClassDiagram", ".class-diagram"),
    USECASE_DIAGRAM(DIAGRAM, "UseCaseDiagram", ".usecase-diagram");

    private final String vpModelType;
    private final Kind vpElementKind;
    private final String suffix;

    ElementType(Kind vpElementKind, String vpModelType, String suffix) {
        this.vpModelType = vpModelType;
        this.vpElementKind = vpElementKind;
        this.suffix = suffix;
    }

    public static ElementType of(Kind theKind, String vpElementType) {
        for (var aValue : values()) {
            if (aValue.kindIs(theKind) && aValue.vpModelTypeIs(vpElementType)) {
                return aValue;
            }
        }
        return null;
    }

    public String getVPModelType() {
        return vpModelType;
    }

    public boolean vpModelTypeIs(String theType) {
        return this.vpModelType.equals(theType);
    }

    public boolean kindIs(Kind theKind ) {
        return this.vpElementKind.equals(theKind);
    }

    public String getSuffix() {
        return suffix;
    }

    public enum Kind {
        MODEL_ELEMENT,
        DIAGRAM_ELEMENT,
        DIAGRAM
    }




}
