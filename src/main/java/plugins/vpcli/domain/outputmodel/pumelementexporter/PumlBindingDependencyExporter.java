package plugins.vpcli.domain.outputmodel.pumelementexporter;

import com.vp.plugin.diagram.IShapeTypeConstants;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/** #21：Bind 特殊依赖（Binding Dependency），渲染与普通依赖一致（`..>`）。 */
@Singleton
public class PumlBindingDependencyExporter extends PumlDependencyExporter {
    @Inject
    public PumlBindingDependencyExporter() {
    }

    @Override
    public String getShapeType() {
        return IShapeTypeConstants.SHAPE_TYPE_BINDING_DEPENDENCY;
    }
}
