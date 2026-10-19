package plugins.vpcli.domain.myuml.mycommon.myelementtype;

import plugins.vpcli.domain.outputmodel.PumlAssociationExporter;

import static com.vp.plugin.model.factory.IModelElementFactory.MODEL_TYPE_ASSOCIATION;

public class MyElementTypeAssociation extends MyElementType {

    public MyElementTypeAssociation(
        PumlAssociationExporter pumlAssociationExporter
    ) {
        super("Association", MODEL_TYPE_ASSOCIATION, pumlAssociationExporter);
    }

    public MyElementTypeAssociation() {
        super("Association", MODEL_TYPE_ASSOCIATION );

    }
}
