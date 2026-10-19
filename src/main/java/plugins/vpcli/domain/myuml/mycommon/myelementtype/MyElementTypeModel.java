package plugins.vpcli.domain.myuml.mycommon.myelementtype;

import plugins.vpcli.domain.outputmodel.PumlModelExporter;

import static com.vp.plugin.model.factory.IModelElementFactory.MODEL_TYPE_MODEL;

public class MyElementTypeModel extends MyElementType {

    public MyElementTypeModel(PumlModelExporter pumlModelExporter) {
        super("Model",
            MODEL_TYPE_MODEL,
            pumlModelExporter
        );
    }

    public MyElementTypeModel() {
        super("Model",
            MODEL_TYPE_MODEL
        );

    }
}
