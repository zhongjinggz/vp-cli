package plugins.vpcli.domain.outputmodel.pumelementexporter;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.model.IGeneralization;
import plugins.vpcli.domain.myuml.mydiagram.mydiagramelement.ProperMyDiagramElement;
import plugins.vpcli.domain.outputmodel.NameDirection;
import plugins.vpcli.domain.outputmodel.PumlDiagram;
import plugins.vpcli.domain.outputmodel.PumlDiagram.LineStyle;
import plugins.vpcli.domain.outputmodel.PumlEdgeEnd;
import plugins.vpcli.domain.outputmodel.PumlEdgeEnd.ShapeKind;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

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
    public void to(PumlDiagram result, ProperMyDiagramElement myDiagramElement, IDiagramElement vpDiagramElement) {
        IGeneralization generalization = (IGeneralization) vpDiagramElement.getModelElement();

        String generalAlias = getPumlAlias(generalization.getFrom());
        String specificAlias = getPumlAlias(generalization.getTo());

        result.writeEdge(
            PumlEdgeEnd.of(generalAlias, "", "", ShapeKind.REVERSE_TRIANGLE),
            PumlEdgeEnd.of(specificAlias, "", "", ShapeKind.NONE),
            LineStyle.SOLID,
            generalization.getName(),
            NameDirection.NONE);
    }
}
