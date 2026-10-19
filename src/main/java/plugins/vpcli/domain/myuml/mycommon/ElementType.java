package plugins.vpcli.domain.myuml.mycommon;

import plugins.vpcli.domain.myuml.myassociation.MyAssociationFactory;
import plugins.vpcli.domain.myuml.mymodelrelationshipcontainer.MyModelRelationshipContainerFactory;

import java.util.Objects;

import static com.vp.plugin.model.factory.IModelElementFactory.*;

public enum ElementType {
    PROJECT("project_has_no_model_type", ".project"),
    DIAGRAM("diagram_has_no_model_type",".diagram"),
    PACKAGE(MODEL_TYPE_PACKAGE, ".package"),
    MODEL(MODEL_TYPE_MODEL, ".model"),
    CLASS(MODEL_TYPE_CLASS, ".class"),
    USECASE(MODEL_TYPE_USE_CASE, ".usecase"),
    ASSOCIATION("diagram_has_no_model_type",".association"),
    RELATIONSHIPS(MODEL_TYPE_MODEL_RELATIONSHIP_CONTAINER, ".relationships");

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
