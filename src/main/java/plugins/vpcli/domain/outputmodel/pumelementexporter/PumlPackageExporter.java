package plugins.vpcli.domain.outputmodel.pumelementexporter;

import com.vp.plugin.diagram.IShapeTypeConstants;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class PumlPackageExporter extends PumlBlockExporter {

    @Inject
    public PumlPackageExporter() {
    }

    @Override
    public String getShapeType() {
        return IShapeTypeConstants.SHAPE_TYPE_PACKAGE;
    }

    @Override
    String getBlockType() {
        return "namespace";
    }
}
