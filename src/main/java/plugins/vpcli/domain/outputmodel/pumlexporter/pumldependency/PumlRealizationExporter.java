package plugins.vpcli.domain.outputmodel.pumlexporter.pumldependency;

import com.vp.plugin.diagram.IConnectorUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import org.jspecify.annotations.NonNull;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.vpmodel.DiagramElements;

@Singleton
//public class PumlRealizationExporter extends PumlElementExporter {
public class PumlRealizationExporter extends PumlDependencyExporter {
    @Inject
    public PumlRealizationExporter() {
    }

    @Override
    public String getShapeType() {
        return IShapeTypeConstants.SHAPE_TYPE_REALIZATION;
    }

    @Override
    protected  @NonNull String getArrow(IDiagramElement vpDiagramElement) {
        if (isRealizingInterfaceBall(vpDiagramElement)) {
            return " -- ";
        } else {
            return " <|.. ";
        }
    }

    private static boolean isRealizingInterfaceBall(IDiagramElement vpDiagramElement) {
        if (vpDiagramElement instanceof IConnectorUIModel) {
            var connector = (IConnectorUIModel) vpDiagramElement;
            return DiagramElements.isInterfaceBall(connector.getFromShape());
        }
        return false;
    }


}
