package plugins.vpcli.domain.myuml.mycommon.myelementtype;

import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;
import plugins.vpcli.domain.outputmodel.PumlElementExporter;

public class MyElementTypePackage extends MyElementType {
    public MyElementTypePackage(
        String name,
        String vpModelType,
        PumlElementExporter pumlElementExporter,
        MyElementFactory myElementFactory
    ) {
        super(name, vpModelType, pumlElementExporter, myElementFactory);
    }
}
