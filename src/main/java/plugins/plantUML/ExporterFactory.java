package plugins.plantUML;

import com.vp.plugin.diagram.IDiagramUIModel;
import org.checkerframework.checker.nullness.qual.NonNull;
import plugins.plantUML.export.*;

import java.io.IOException;

public class ExporterFactory {
    @NonNull
    public ActivityDiagramExporter createActivityDiagramExporter(IDiagramUIModel diagram) {
        return new ActivityDiagramExporter(diagram);
    }

    @NonNull
    public StateDiagramExporter createStateDiagramExporter(IDiagramUIModel diagram) {
        return new StateDiagramExporter(diagram);
    }

    @NonNull
    public UseCaseDiagramExporter createUseCaseDiagramExporter(IDiagramUIModel diagram) {
        return new UseCaseDiagramExporter(diagram);
    }

    @NonNull
    public SequenceDiagramExporter createSequenceDiagramExporter(IDiagramUIModel diagram) {
        return new SequenceDiagramExporter(diagram);
    }

    @NonNull
    public ComponentDeploymentDiagramExporter createComponentDeploymentDiagramExporter(IDiagramUIModel diagram) throws IOException {
        return new ComponentDeploymentDiagramExporter(diagram);
    }

    @NonNull
    public ClassDiagramExporter createClassDiagramExporter(IDiagramUIModel diagram) {
        return new ClassDiagramExporter(diagram);
    }
}
