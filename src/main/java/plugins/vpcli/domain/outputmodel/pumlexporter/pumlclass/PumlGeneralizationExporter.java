package plugins.vpcli.domain.outputmodel.pumlexporter.pumlclass;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.model.IGeneralization;
import org.jetbrains.annotations.NotNull;
import plugins.vpcli.domain.outputmodel.pumldiagram.NameDirection;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlDiagram;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlDiagram.LineStyle;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlEdgeEnd;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlEdgeEnd.ShapeKind;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.outputmodel.pumlexporter.PumlDirectedRelationshipExporter;
import plugins.vpcli.domain.outputmodel.pumlexporter.PumlElementExporter;
import plugins.vpcli.domain.outputmodel.pumlexporter.pumldependency.PumlDependencyExporter;

@Singleton
public class PumlGeneralizationExporter extends PumlDirectedRelationshipExporter {
    @Inject
    public PumlGeneralizationExporter() {
    }

    @Override
    public String getShapeType() {
        return IShapeTypeConstants.SHAPE_TYPE_GENERALIZATION;
    }

    @Override
    protected @NotNull String getArrow() {
        return " <|-- ";
    }
}
