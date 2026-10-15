package plugins.vpcli.domain.myuml.mycommon.myelementtype;

import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;
import plugins.vpcli.domain.outputmodel.PumlElementExporter;
import plugins.vpcli.domain.outputmodel.PumlUseCaseExporter;

public class MyElementTypeUseCase extends MyElementType {
    public MyElementTypeUseCase(
        String name,
        String vpModelType,
        PumlElementExporter pumlElementExporter,
        MyElementFactory myElementFactory
    ) {
        super(name, vpModelType, pumlElementExporter, myElementFactory);
    }

    public MyElementTypeUseCase(String name, String vpModelType, PumlUseCaseExporter pumlUseCaseExporter) {
        super(name, vpModelType, pumlUseCaseExporter);
    }
}
