package plugins.vpcli.domain.myuml.mycommon.myelementtype;

import plugins.vpcli.domain.outputmodel.PumlUseCaseExporter;

import static com.vp.plugin.model.factory.IModelElementFactory.MODEL_TYPE_USE_CASE;

public class MyElementTypeUseCase extends MyElementType {

    public MyElementTypeUseCase(PumlUseCaseExporter pumlUseCaseExporter) {
        super("UseCase",
            MODEL_TYPE_USE_CASE,
            pumlUseCaseExporter
        );
    }

    public MyElementTypeUseCase() {

        super("UseCase"
            , MODEL_TYPE_USE_CASE
        );
    }
}
