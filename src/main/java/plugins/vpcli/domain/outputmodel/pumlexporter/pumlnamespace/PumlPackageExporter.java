package plugins.vpcli.domain.outputmodel.pumlexporter.pumlnamespace;

import com.vp.plugin.diagram.IShapeTypeConstants;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.outputmodel.pumlexporter.PumlBlockExporter;

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
    public String getBlockType() {
        return "namespace";
    }
}
