package plugins.vpcli.domain.outputmodel.pumelementexporter;

import com.vp.plugin.diagram.IShapeTypeConstants;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class PumlModelExporter extends PumlPackageExporter {
    @Inject
    public PumlModelExporter() {
    }

    @Override
    public String getShapeType() {
        return IShapeTypeConstants.SHAPE_TYPE_MODEL;
    }

    @Override
    String getBlockSuffix() {
        return "<<model>>";
    }
}
