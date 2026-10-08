package plugins.vpcli.domain.myuml.mycommon;

import java.util.Objects;

import static com.vp.plugin.model.factory.IModelElementFactory.*;

public enum ElementType {
    PACKAGE(MODEL_TYPE_PACKAGE, ".package"),
    MODEL(MODEL_TYPE_MODEL, ".model"),
    CLASS(MODEL_TYPE_CLASS, ".class"),
    USECASE(MODEL_TYPE_USE_CASE, ".usecase"),
    DIAGRAM("",".diagram");

    private final String vpModelType;
    private final String suffix;

    ElementType(String vpModelType, String suffix) {
        this.vpModelType = vpModelType;
        this.suffix = suffix;
    }

    public static ElementType of(String vpElementType) {
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
