package plugins.vpcli.domain.myuml.mycommon.myelementtype;

import static com.vp.plugin.model.factory.IModelElementFactory.*;

public class MyElementTypes {
    public final MyElementTypeProject PROJECT;
    public final MyElementTypeClass CLASS;
    public final MyElementTypePackage PACKAGE;
    public final MyElementTypeModel MODEL;
    public final MyElementTypeUseCase USE_CASE;
    public final MyElementTypeDiagram DIAGRAM;
    public final MyElementTypeAssociation ASSOCIATION;

    public MyElementTypes(
        MyElementTypeProject myElementTypeProject,
        MyElementTypeClass myElementTypeClass,
        MyElementTypePackage myElementTypePackage,
        MyElementTypeModel myElementTypeModel,
        MyElementTypeUseCase myElementTypeUseCase,
        MyElementTypeDiagram elementTypeDiagram,
        MyElementTypeAssociation myElementTypeAssociation
    ) {
        this.PROJECT = myElementTypeProject;
        this.CLASS = myElementTypeClass;
        this.PACKAGE = myElementTypePackage;
        this.MODEL = myElementTypeModel;
        this.USE_CASE = myElementTypeUseCase;
        this.DIAGRAM = elementTypeDiagram;
        this.ASSOCIATION = myElementTypeAssociation;
    }

    public MyElementType ofVPModelType(String vpModelType) {
        switch (vpModelType) {
            case MODEL_TYPE_PACKAGE:
                return PACKAGE;
            case MODEL_TYPE_MODEL:
                return MODEL;
            case MODEL_TYPE_CLASS:
                return CLASS;
            case MODEL_TYPE_USE_CASE:
                return USE_CASE;
            case MODEL_TYPE_ASSOCIATION:
                return ASSOCIATION;
            default:
                throw new IllegalArgumentException("Unknown visual paradigm model type: " + vpModelType);
        }
    }
}
