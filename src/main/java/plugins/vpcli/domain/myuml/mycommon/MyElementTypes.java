package plugins.vpcli.domain.myuml.mycommon;

import plugins.vpcli.domain.myuml.mycommon.myelementtype.*;

import static com.vp.plugin.model.factory.IModelElementFactory.*;

public class MyElementTypes {
    public final MyElementTypeProject PROJECT;
    public final MyElementTypeClass CLASS;
    public final MyElementTypePackage PACKAGE;
    public final MyElementTypeModel MODEL;
    public final MyElementTypeUseCase USE_CASE;
    public final MyElementTypeDiagram DIAGRAM;
    public final MyElementTypeAssociation ASSOCIATION;
    public final MyElementTypeModelRelationshipContainer MODEL_RELATIONSHIP_CONTAINER;

    public MyElementTypes(
        MyElementTypeProject myElementTypeProject,
        MyElementTypeClass myElementTypeClass,
        MyElementTypePackage myElementTypePackage,
        MyElementTypeModel myElementTypeModel,
        MyElementTypeUseCase myElementTypeUseCase,
        MyElementTypeDiagram myElementTypeDiagram,
        MyElementTypeAssociation myElementTypeAssociation,
        MyElementTypeModelRelationshipContainer myElementTypeModelRelationshipContainer
    ) {
        this.PROJECT = myElementTypeProject;

        //call by MyElementTypes to create the cycle dependency:
        // MyElementTypes --> MyElementType --> MyElementFactory
        //     ^----------------------------------------|
        this.CLASS = myElementTypeClass;
        this.CLASS.getMyElementFactory().setMyElementTypes(this);

        this.PACKAGE = myElementTypePackage;
        this.PACKAGE.getMyElementFactory().setMyElementTypes(this);

        this.MODEL = myElementTypeModel;
        this.MODEL.getMyElementFactory().setMyElementTypes(this);

        this.USE_CASE = myElementTypeUseCase;
        this.USE_CASE.getMyElementFactory().setMyElementTypes(this);

        this.DIAGRAM = myElementTypeDiagram;
        //this.DIAGRAM.getMyElementFactory().setMyElementTypes(this);

        this.ASSOCIATION = myElementTypeAssociation;
        this.ASSOCIATION.getMyElementFactory().setMyElementTypes(this);

        this.MODEL_RELATIONSHIP_CONTAINER = myElementTypeModelRelationshipContainer;
        this.MODEL_RELATIONSHIP_CONTAINER.getMyElementFactory().setMyElementTypes(this);
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
            case MODEL_TYPE_MODEL_RELATIONSHIP_CONTAINER:
                return MODEL_RELATIONSHIP_CONTAINER;
            default:
                throw new IllegalArgumentException("Unknown visual paradigm model type: " + vpModelType);
        }
    }
}
