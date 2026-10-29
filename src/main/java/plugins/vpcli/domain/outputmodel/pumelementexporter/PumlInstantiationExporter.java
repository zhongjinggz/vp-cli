package plugins.vpcli.domain.outputmodel.pumelementexporter;

import com.vp.plugin.diagram.IShapeTypeConstants;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/** #21：Instantiate 特殊依赖（Instantiation），渲染与普通依赖一致（`..>`）。 */
@Singleton
public class PumlInstantiationExporter extends PumlDependencyExporter {
    @Inject
    public PumlInstantiationExporter() {
    }

    @Override
    public String getShapeType() {
        return IShapeTypeConstants.SHAPE_TYPE_INSTANTIATION;
    }
}
