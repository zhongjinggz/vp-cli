package plugins.vpcli.application.writers;

import org.checkerframework.checker.nullness.qual.NonNull;
import plugins.vpcli.domain.mydiagram.*;
import plugins.vpcli.drivenadapter.FileFactory;

public class WriterFactory {
    private FileFactory fileFactory;
    public WriterFactory(FileFactory fileFactory) {
        this.fileFactory = fileFactory;
    }

    @NonNull
    public ActivityUMLWriter createActivityUMLWriter(MyActivityDiagram exporter) {
        return new ActivityUMLWriter(
            exporter.getNotes(),
            exporter.getRootNode()
        );
    }

    @NonNull
    public StateUMLWriter createStateUMLWriter(MyStateDiagram exporter) {
        return new StateUMLWriter(
            exporter.getNotes(),
            exporter.getStateDatas(),
            exporter.getTransitions(),
            exporter.getChoices(),
            exporter.getHistories(),
            exporter.getForkJoins()
        );
    }

    @NonNull
    public UseCaseWriter createUseCaseWriter(MyUseCaseDiagram exporter) {
        return new UseCaseWriter(
            exporter.getExportedUseCases(),
            exporter.getExportedRelationships(),
            exporter.getExportedPackages(),
            exporter.getExportedActors(),
            exporter.getNotes()
        );
    }

    @NonNull
    public SequenceUMLWriter createSequenceUMLWriter(MySequenceDiagram exporter) {
        return new SequenceUMLWriter(
            exporter.getNotes(),
            exporter.getExportedInteractionActors(),
            exporter.getExportedLifelines(),
            exporter.getExportedMessages(),
            exporter.getExportedFragments(),
            exporter.getExportedRefs(),
            exporter.getExportedAnchors()
        );
    }

    @NonNull
    public ComponentDeploymentUMLWriter createComponentDeploymentUMLWriter(MyComponentDeploymentDiagram exporter) {
        return new ComponentDeploymentUMLWriter(
            exporter.getNotes(),
            exporter.getExportedComponents(),
            exporter.getExportedInterfaces(),
            exporter.getExportedArtifacts(),
            exporter.getExportedPackages(),
            exporter.getRelationshipDatas()
        );
    }

    @NonNull
    public ClassDiagramWriter createClassDiagramWriter(MyClassDiagram diagram) {
        return new ClassDiagramWriter(
            diagram, this.fileFactory
        );
    }
}
