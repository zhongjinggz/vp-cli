package plugins.vpcli.domain.outputmodel.pumlexporter.pumldependency;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import org.jetbrains.annotations.NotNull;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.outputmodel.pumlexporter.PumlDirectedRelationshipExporter;

@Singleton
public class PumlDependencyExporter extends PumlDirectedRelationshipExporter {
    @Inject
    public PumlDependencyExporter() {
    }

    @Override
    public String getShapeType() {
        return IShapeTypeConstants.SHAPE_TYPE_DEPENDENCY;
    }

    protected  @NotNull String getArrow(IDiagramElement vpDiagramElement) {
        return " ..> ";
    }
}
