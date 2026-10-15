package plugins.vpcli.domain.myuml.mycommon.myelementtype;

import com.vp.plugin.model.factory.IModelElementFactory;
import plugins.vpcli.domain.outputmodel.PumlClassExporter;

public class MyElementTypeClass extends MyElementType {

    public MyElementTypeClass(PumlClassExporter pumlClassExporter) {
        super("Class",
            IModelElementFactory.MODEL_TYPE_CLASS,
            pumlClassExporter
        );
    }
}
