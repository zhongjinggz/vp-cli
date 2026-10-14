package plugins.vpcli.domain.myuml.mycommon.myelementtype;

import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;
import plugins.vpcli.domain.outputmodel.PumlElementExporter;

public class MyElementTypeClass extends MyElementType {
    public MyElementTypeClass(
        String name,
        String vpModelType,
        PumlElementExporter pumlElementExporter,
        MyElementFactory myElementFactory
    ) {
        super(name, vpModelType, pumlElementExporter, myElementFactory);
    }
}
