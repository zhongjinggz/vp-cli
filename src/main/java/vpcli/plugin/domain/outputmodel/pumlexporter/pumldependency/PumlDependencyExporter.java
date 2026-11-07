package vpcli.plugin.domain.outputmodel.pumlexporter.pumldependency;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jspecify.annotations.NonNull;
import vpcli.plugin.domain.outputmodel.pumlexporter.PumlDirectedRelationshipExporter;

@Singleton
public class PumlDependencyExporter extends PumlDirectedRelationshipExporter {
    @Inject
    public PumlDependencyExporter() {
    }

    @Override
    public String getShapeType() {
        return IShapeTypeConstants.SHAPE_TYPE_DEPENDENCY;
    }

    @Override
    protected  @NonNull String getArrow(IDiagramElement vpDiagramElement) {
        return " ..> ";
    }
}
