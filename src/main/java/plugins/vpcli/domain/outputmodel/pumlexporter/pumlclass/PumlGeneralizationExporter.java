package plugins.vpcli.domain.outputmodel.pumlexporter.pumlclass;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.model.IGeneralization;
import plugins.vpcli.domain.outputmodel.pumldiagram.NameDirection;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlDiagram;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlDiagram.LineStyle;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlEdgeEnd;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlEdgeEnd.ShapeKind;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.outputmodel.pumlexporter.PumlElementExporter;

@Singleton
public class PumlGeneralizationExporter extends PumlElementExporter {
    @Inject
    public PumlGeneralizationExporter() {
    }

    @Override
    public String getShapeType() {
        return IShapeTypeConstants.SHAPE_TYPE_GENERALIZATION;
    }

    @Override
    public void export(PumlDiagram result, IDiagramElement vpDiagramElement) {
        IGeneralization generalization = (IGeneralization) vpDiagramElement.getModelElement();

        String generalAlias = getPumlAlias(generalization.getFrom());
        String specificAlias = getPumlAlias(generalization.getTo());

        result.writeEdge(
            PumlEdgeEnd.of(generalAlias, "", "", ShapeKind.REVERSE_TRIANGLE),
            PumlEdgeEnd.of(specificAlias, "", "", ShapeKind.NONE),
            LineStyle.SOLID,
            buildEdgeLabel(generalization.getName(), generalization),
            NameDirection.NONE);
    }
}
