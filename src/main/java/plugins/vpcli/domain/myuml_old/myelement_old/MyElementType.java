package plugins.vpcli.domain.myuml_old.myelement_old;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plugins.vpcli.domain.myuml.myunknownelement.UnimplementedElement;

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
    ASSOCIATION_END(MODEL_TYPE_ASSOCIATION_END, ".association-end"),
    DEPENDENCY(MODEL_TYPE_DEPENDENCY, ".dependency"),
    GENERALIZATION(MODEL_TYPE_GENERALIZATION, ".generalization"),
    RELATIONSHIPS(MODEL_TYPE_MODEL_RELATIONSHIP_CONTAINER, ".relationships"),
    STEREOTYPE(MODEL_TYPE_STEREOTYPE, ".stereotype"),
    UNIMPLEMENTED_ELEMENT(UnimplementedElement.MODEL_TYPE_TEXT, ".unimplemented-element");

    private static final Logger log = LoggerFactory.getLogger(MyElementType.class);

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
