package plugins.vpcli.domain.myuml.mycommon.myelementtype;

import plugins.vpcli.domain.outputmodel.PumlModelRelationshipContainerExporter;

import static com.vp.plugin.model.factory.IModelElementFactory.MODEL_TYPE_MODEL_RELATIONSHIP_CONTAINER;

public class MyElementTypeModelRelationshipContainer extends MyElementType {
    public MyElementTypeModelRelationshipContainer(PumlModelRelationshipContainerExporter pumlModelRelationshipContainerExporter) {
        super("ModelRelationshipContainer",
            MODEL_TYPE_MODEL_RELATIONSHIP_CONTAINER,
            pumlModelRelationshipContainerExporter
        );
    }

    public MyElementTypeModelRelationshipContainer() {
        super("ModelRelationshipContainer"
            , MODEL_TYPE_MODEL_RELATIONSHIP_CONTAINER
        );
    }
}
