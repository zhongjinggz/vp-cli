package plugins.vpcli.domain.outputmodel.pumlexporter.pumlnamespace;

import com.vp.plugin.diagram.IShapeTypeConstants;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.outputmodel.pumlexporter.pumldependency.PumlDependencyExporter;

/** #21：Merge 特殊依赖，渲染与普通依赖一致（`..>`）。 */
@Singleton
public class PumlMergeExporter extends PumlDependencyExporter {
    @Inject
    public PumlMergeExporter() {
    }

    @Override
    public String getShapeType() {
        return IShapeTypeConstants.SHAPE_TYPE_MERGE;
    }
}
