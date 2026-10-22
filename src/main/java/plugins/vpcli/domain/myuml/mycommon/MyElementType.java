package plugins.vpcli.domain.myuml.mycommon;

import java.util.Objects;

import static com.vp.plugin.model.factory.IModelElementFactory.*;

public enum MyElementType {
    PROJECT("project_has_no_model_type", ".project"),
    DIAGRAM("diagram_has_no_model_type", ".diagram"),
    PACKAGE(MODEL_TYPE_PACKAGE, ".package"),
    MODEL(MODEL_TYPE_MODEL, ".model"),
    CLASS(MODEL_TYPE_CLASS, ".class"),
    USE_CASE(MODEL_TYPE_USE_CASE, ".usecase"),
    ASSOCIATION("diagram_has_no_model_type", ".association"),
    ASSOCIATION_END(MODEL_TYPE_ASSOCIATION_END, ".associationend"),
    RELATIONSHIPS(MODEL_TYPE_MODEL_RELATIONSHIP_CONTAINER, ".relationships"),
    STEREOTYPE(MODEL_TYPE_STEREOTYPE, ".stereotype");

    private final String vpModelType;
    private final String suffix;

    MyElementType(String vpModelType, String suffix) {
        this.vpModelType = vpModelType;
        this.suffix = suffix;
    }

    public static MyElementType of(String vpElementType) {
        for (var aValue : values()) {
            if (aValue.vpModelTypeIs(vpElementType)) {
                return aValue;
            }
        }
        return null;
    }

    public String getVPModelType() {
        return vpModelType;
    }

    public boolean vpModelTypeIs(String theType) {
        //return this.vpModelType.equals(theType);
        return Objects.equals(this.vpModelType, theType);
    }

    public String getSuffix() {
        return suffix;
    }

    public boolean isModelElement() {
        return !Objects.equals(this, DIAGRAM);
    }
}
