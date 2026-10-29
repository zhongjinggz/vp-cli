package plugins.vpcli.domain.outputmodel.pumelementexporter;

import com.vp.plugin.diagram.IShapeTypeConstants;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/** #21：Permit 特殊依赖（Permission），渲染与普通依赖一致（`..>`）。 */
@Singleton
public class PumlPermissionExporter extends PumlDependencyExporter {
    @Inject
    public PumlPermissionExporter() {
    }

    @Override
    public String getShapeType() {
        return IShapeTypeConstants.SHAPE_TYPE_PERMISSION;
    }
}
