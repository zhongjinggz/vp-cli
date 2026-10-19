package plugins.vpcli.domain.myuml.mycommon.myelementtype;

import plugins.vpcli.domain.outputmodel.PumlPackageExporter;

import static com.vp.plugin.model.factory.IModelElementFactory.MODEL_TYPE_PACKAGE;

public class MyElementTypePackage extends MyElementType {

    public MyElementTypePackage(PumlPackageExporter pumlPackageExporter) {
        super("Package",
            MODEL_TYPE_PACKAGE,
            pumlPackageExporter
        );
    }

    public MyElementTypePackage() {
        super("Package"
            , MODEL_TYPE_PACKAGE
        );
    }
}
