package vpcli.plugin.domain.vpmodelvisitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static com.vp.plugin.model.factory.IModelElementFactory.*;

public enum ViElementType {
    UNIMPLEMENTED("Unimplemented", ".unimplemented"),
    PROJECT("project_has_no_model_type", ".project"),
    DIAGRAM("diagram_has_no_model_type", ".diagram"),
    PACKAGE(MODEL_TYPE_PACKAGE, ".package"),
    MODEL(MODEL_TYPE_MODEL, ".model"),
    CLASS(MODEL_TYPE_CLASS, ".class"),
    NARY(MODEL_TYPE_NARY, ".nary"),
    USE_CASE(MODEL_TYPE_USE_CASE, ".usecase"),
    ASSOCIATION("diagram_has_no_model_type", ".association"),
    ASSOCIATION_END(MODEL_TYPE_ASSOCIATION_END, ".association-end"),
    DEPENDENCY(MODEL_TYPE_DEPENDENCY, ".dependency"),
    /* #21：Usage / Bind / Permit / Abstraction 各有独立 model type，补登记导出映射。 */
    USAGE(MODEL_TYPE_USAGE, ".usage"),
    BINDING_DEPENDENCY(MODEL_TYPE_BINDING_DEPENDENCY, ".binding-dependency"),
    PERMISSION(MODEL_TYPE_PERMISSION, ".permission"),
    ABSTRACTION(MODEL_TYPE_ABSTRACTION, ".abstraction"),
    GENERALIZATION(MODEL_TYPE_GENERALIZATION, ".generalization"),
    RELATIONSHIPS(MODEL_TYPE_MODEL_RELATIONSHIP_CONTAINER, ".relationships"),
    STEREOTYPE(MODEL_TYPE_STEREOTYPE, ".stereotype");

    private static final Logger log = LoggerFactory.getLogger(ViElementType.class);

    private final String vpModelType;
    private final String suffix;

    ViElementType(String vpModelType, String suffix) {
        this.vpModelType = vpModelType;
        this.suffix = suffix;
    }

    public static ViElementType of(String vpElementType) {
        for (var aValue : values()) {
            if (aValue.vpModelTypeIs(vpElementType)) {
                return aValue;
            }
        }
        log.warn("Unimplemented vp model element type: {}", vpElementType);
        return UNIMPLEMENTED;
    }

    public String getVPModelType() {
        return vpModelType;
    }

    public boolean vpModelTypeIs(String theType) {
        return Objects.equals(this.vpModelType, theType);
    }

    public String getSuffix() {
        return suffix;
    }

    public boolean isModelElement() {
        return !Objects.equals(this, DIAGRAM);
    }
}
