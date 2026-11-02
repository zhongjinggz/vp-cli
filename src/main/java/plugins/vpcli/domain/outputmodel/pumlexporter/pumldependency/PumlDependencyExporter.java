package plugins.vpcli.domain.outputmodel.pumlexporter.pumldependency;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.model.IRelationship;
import org.jspecify.annotations.NonNull;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlDiagram;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.outputmodel.pumlexporter.PumlElementExporter;

import java.util.List;

@Singleton
public class PumlDependencyExporter extends PumlElementExporter {
    @Inject
    public PumlDependencyExporter() {
    }

    @Override
    public String getShapeType() {
        return IShapeTypeConstants.SHAPE_TYPE_DEPENDENCY;
    }

    @Override
    public void export(PumlDiagram result, IDiagramElement vpDiagramElement) {
        IRelationship dependency = (IRelationship) vpDiagramElement.getModelElement();

        String fromAlias = getPumlAlias(dependency.getFrom());
        String toAlias = getPumlAlias(dependency.getTo());

        result.writeTerminalShape(
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
            result.append(": ");
        }
        for (String label : labels) {
            result.append("<<");
            result.append(label);
            result.append(">>");
        }
        return result.toString();
    }

    protected  @NonNull String getArrow() {
        return " ..> ";
    }
}
