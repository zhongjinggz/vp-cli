package plugins.vpcli.domain.myuml.mycommon.myelementtype;

import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;
import plugins.vpcli.domain.outputmodel.PumlElementExporter;
import plugins.vpcli.domain.outputmodel.PumlModelExporter;

public class MyElementTypeModel extends MyElementType {
    public MyElementTypeModel(
        String name,
        String vpModelType,
        PumlElementExporter pumlElementExporter,
        MyElementFactory myElementFactory
    ) {
        super(name, vpModelType, pumlElementExporter, myElementFactory);
    }

    public MyElementTypeModel(String name, String vpModelType, PumlModelExporter pumlModelExporter) {
        super(name, vpModelType, pumlModelExporter);
    }
}
