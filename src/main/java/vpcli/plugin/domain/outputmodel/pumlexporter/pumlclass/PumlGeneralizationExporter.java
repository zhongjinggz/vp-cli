package vpcli.plugin.domain.outputmodel.pumlexporter.pumlclass;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import org.jetbrains.annotations.NotNull;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import vpcli.plugin.domain.outputmodel.pumlexporter.PumlDirectedRelationshipExporter;

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
    protected @NotNull String getArrow(IDiagramElement vpDiagramElement) {
        return " <|-- ";
    }
}
