package plugins.vpcli.domain.vpmodelvisitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static com.vp.plugin.model.factory.IModelElementFactory.*;

// vpmodelvisitor 自有的"VP modelType ↔ 后缀"纯映射，替代对 myuml 的 MyElementType 依赖。
// 与 MyElementType 行为同构，但不引用任何 myuml 领域类。
public enum ViElementType {
    PROJECT("project_has_no_model_type", ".project"),
    DIAGRAM("diagram_has_no_model_type", ".diagram"),
    PACKAGE(MODEL_TYPE_PACKAGE, ".package"),
    MODEL(MODEL_TYPE_MODEL, ".model"),
    CLASS(MODEL_TYPE_CLASS, ".class"),
    USE_CASE(MODEL_TYPE_USE_CASE, ".usecase"),
    ASSOCIATION("diagram_has_no_model_type", ".association"),
    ASSOCIATION_END(MODEL_TYPE_ASSOCIATION_END, ".association-end"),
    DEPENDENCY(MODEL_TYPE_DEPENDENCY, ".dependency"),
    GENERALIZATION(MODEL_TYPE_GENERALIZATION, ".generalization"),
    RELATIONSHIPS(MODEL_TYPE_MODEL_RELATIONSHIP_CONTAINER, ".relationships"),
    STEREOTYPE(MODEL_TYPE_STEREOTYPE, ".stereotype"),
    UNIMPLEMENTED_ELEMENT("UnimplementedElement", ".unimplemented-element");

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
        return UNIMPLEMENTED_ELEMENT;
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
