package plugins.vpcli.domain.outputmodel.pumlexporter;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.model.IRelationship;
import org.jetbrains.annotations.NotNull;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlDiagram;

import java.util.List;

public abstract class PumlDirectedRelationshipExporter extends PumlElementExporter {
    private static @NotNull String formatStereotype(String label) {
        return "<<" + label + ">>";
    }

    @Override
    public String getShapeType() {
        return "";
    }

    @Override
    public void export(PumlDiagram result, IDiagramElement vpDiagramElement) {
        IRelationship dependency = (IRelationship) vpDiagramElement.getModelElement();

        String fromAlias = getPumlAlias(dependency.getFrom());
        String toAlias = getPumlAlias(dependency.getTo());

        result.writeText(
            toText(
                fromAlias,
                toAlias,
                getStereotypes(dependency)
            )
        );
    }

    private String toText(String fromAlias, String toAlias, List<String> labels) {
        StringBuilder result = new StringBuilder();
        result.append(fromAlias);
        result.append(getArrow());
        result.append(toAlias);
        if (!labels.isEmpty()) {
            result.append(":");
        }
        for (String label : labels) {
            result.append(" ").append(PumlDirectedRelationshipExporter.formatStereotype(label));
        }
        return result.toString();
    }

    protected abstract String getArrow();
}
