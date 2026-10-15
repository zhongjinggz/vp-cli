package plugins.vpcli.domain.myuml.mycommon.myelementtype;

import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;
import plugins.vpcli.domain.outputmodel.PumlAssociationExporter;
import plugins.vpcli.domain.outputmodel.PumlClassExporter;
import plugins.vpcli.domain.outputmodel.PumlElementExporter;

public class MyElementTypeAssociation extends MyElementType {
    public MyElementTypeAssociation(
        String name,
        String vpModelType,
        PumlElementExporter pumlElementExporter,
        MyElementFactory myElementFactory
    ) {
        super(name, vpModelType, pumlElementExporter, myElementFactory);
    }

    public MyElementTypeAssociation(
        String name,
        String vpModelType,
        PumlAssociationExporter pumlAssociationExporter
    ) {
        super(name, vpModelType, pumlAssociationExporter);
    }
}
