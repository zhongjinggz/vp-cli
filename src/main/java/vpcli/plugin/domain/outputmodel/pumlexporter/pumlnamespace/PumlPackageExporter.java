package vpcli.plugin.domain.outputmodel.pumlexporter.pumlnamespace;

import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.model.IModelElement;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import vpcli.plugin.domain.outputmodel.pumlexporter.PumlBlockExporter;

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
    public String getBlockType(IModelElement vpElement) {
        return "namespace";
    }
}
