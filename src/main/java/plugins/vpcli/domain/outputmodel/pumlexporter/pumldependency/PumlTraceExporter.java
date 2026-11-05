package plugins.vpcli.domain.outputmodel.pumlexporter.pumldependency;

import com.vp.plugin.diagram.IShapeTypeConstants;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/** #21：Access 特殊依赖，渲染与普通依赖一致（`..>`）。 */
@Singleton
public class PumlTraceExporter extends PumlDependencyExporter {
    @Inject
    public PumlTraceExporter() {
    }

    @Override
    public String getShapeType() {
        return IShapeTypeConstants.SHAPE_TYPE_TRACE;
    }
}
