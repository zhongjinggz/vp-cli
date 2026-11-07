package plugins.vpcli.domain.outputmodel.pumlexporter.pumldependency;

import com.vp.plugin.diagram.IConnectorUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jspecify.annotations.NonNull;
import plugins.vpcli.domain.vpmodel.DiagramElements;

/**
 * #21：Usage 特殊依赖，渲染与普通依赖一致（`..>`）。
 */
@Singleton
public class PumlUsageExporter extends PumlDependencyExporter {
    @Inject
    public PumlUsageExporter() {
    }

    @Override
    public String getShapeType() {
        return IShapeTypeConstants.SHAPE_TYPE_USAGE;
    }

    @Override
    protected @NonNull String getArrow(IDiagramElement vpDiagramElement) {
        if (isUseInterfaceBall(vpDiagramElement)) {
            return " --( ";
        } else {
            return super.getArrow(vpDiagramElement);
        }
    }

    private static boolean isUseInterfaceBall(IDiagramElement vpDiagramElement) {
        boolean useInterfaceBall = false;
        if (vpDiagramElement instanceof IConnectorUIModel) {
            var connector = (IConnectorUIModel) vpDiagramElement;
            if (DiagramElements.isInterfaceBall(connector.getToShape())) {
                useInterfaceBall = true;
            }
        }
        return useInterfaceBall;
    }
}
