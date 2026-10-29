package plugins.vpcli.domain.outputmodel.pumelementexporter;

import com.vp.plugin.diagram.IShapeTypeConstants;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/** #21：Substitute 特殊依赖（Substitution），渲染与普通依赖一致（`..>`）。 */
@Singleton
public class PumlSubstitutionExporter extends PumlDependencyExporter {
    @Inject
    public PumlSubstitutionExporter() {
    }

    @Override
    public String getShapeType() {
        return IShapeTypeConstants.SHAPE_TYPE_SUBSTITUTION;
    }
}
