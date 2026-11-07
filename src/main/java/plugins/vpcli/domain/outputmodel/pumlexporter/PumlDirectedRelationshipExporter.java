package plugins.vpcli.domain.outputmodel.pumlexporter;

import com.vp.plugin.diagram.IConnectorUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.model.IRelationship;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlDiagram;

import static plugins.vpcli.util.Strings.isNotBlank;

public abstract class PumlDirectedRelationshipExporter extends PumlElementExporter {

    @Override
    public String getShapeType() {
        return "";
    }

    @Override
    public void export(PumlDiagram result, IDiagramElement vpDiagramElement) {
        IRelationship relationship = (IRelationship) vpDiagramElement.getModelElement();

        String fromAlias;
        String toAlias;
        IConnectorUIModel connector = (IConnectorUIModel) vpDiagramElement;
        fromAlias = pumlAlias(connector.getFromShape());
        toAlias = pumlAlias(connector.getToShape());

        result.writeText(
            toText(
                fromAlias,
                toAlias,
                getArrow(vpDiagramElement),
                getStereotypesText(relationship)
            )
        );
    }

    private String toText(
        String fromAlias,
        String toAlias,
        String arrow,
        String stereotypes
    ) {
        StringBuilder result = new StringBuilder();
        result.append(fromAlias);
        result.append(arrow);
        result.append(toAlias);
        if (isNotBlank(stereotypes)) {
            result.append(": ").append(stereotypes);
        }

        return result.toString();
    }

    protected abstract String getArrow(IDiagramElement vpDiagramElement);
}
