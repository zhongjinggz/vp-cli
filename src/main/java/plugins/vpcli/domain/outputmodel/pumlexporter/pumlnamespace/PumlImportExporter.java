package plugins.vpcli.domain.outputmodel.pumlexporter.pumlnamespace;

import com.vp.plugin.diagram.IShapeTypeConstants;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.outputmodel.pumlexporter.pumldependency.PumlDependencyExporter;

/** #21：Import 特殊依赖，渲染与普通依赖一致（`..>`）。 */
@Singleton
public class PumlImportExporter extends PumlDependencyExporter {
    @Inject
    public PumlImportExporter() {
    }

    @Override
    public String getShapeType() {
        return IShapeTypeConstants.SHAPE_TYPE_IMPORT;
    }
}
