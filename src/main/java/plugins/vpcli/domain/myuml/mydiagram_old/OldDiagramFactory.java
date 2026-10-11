package plugins.vpcli.domain.myuml.mydiagram_old;

import com.vp.plugin.diagram.IDiagramUIModel;
import org.checkerframework.checker.nullness.qual.NonNull;

public class OldDiagramFactory {
    @NonNull
    public MyActivityDiagram createActivityDiagramExporter(IDiagramUIModel diagram) {
        return new MyActivityDiagram(diagram);
    }

    @NonNull
    public MyStateDiagram createStateDiagramExporter(IDiagramUIModel diagram) {
        return new MyStateDiagram(diagram);
    }

    @NonNull
    public MyUseCaseDiagram createUseCaseDiagramExporter(IDiagramUIModel diagram) {
        return new MyUseCaseDiagram(diagram);
    }

    @NonNull
    public MySequenceDiagram createSequenceDiagramExporter(IDiagramUIModel diagram) {
        return new MySequenceDiagram(diagram);
    }

    @NonNull
    public MyComponentDeploymentDiagram createComponentDeploymentDiagramExporter(IDiagramUIModel diagram) {
        return new MyComponentDeploymentDiagram(diagram);
    }

    @NonNull
    public OldClassDiagram createClassDiagram(IDiagramUIModel diagramUIModel) {
        OldClassDiagram diagram = new OldClassDiagram(diagramUIModel);
        diagram.extract();
        return diagram;
    }
}
