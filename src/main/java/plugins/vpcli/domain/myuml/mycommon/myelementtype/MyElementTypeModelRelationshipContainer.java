package plugins.vpcli.domain.myuml.mycommon.myelementtype;

import plugins.vpcli.domain.outputmodel.PumlModelRelationshipContainerExporter;
import plugins.vpcli.domain.outputmodel.PumlPackageExporter;

public class MyElementTypeModelRelationshipContainer extends MyElementType{
    public MyElementTypeModelRelationshipContainer(String name, String vpModelType, PumlModelRelationshipContainerExporter pumlModelRelationshipContainerExporter) {
        super(name, vpModelType, pumlModelRelationshipContainerExporter);
    }
}
