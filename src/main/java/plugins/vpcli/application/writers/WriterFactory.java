package plugins.vpcli.application.writers;

import org.checkerframework.checker.nullness.qual.NonNull;
import plugins.vpcli.domain.mydiagram.*;

public class WriterFactory {
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
    public ClassUMLWriter createClassUMLWriter(MyClassDiagram exporter) {
        return new ClassUMLWriter(
            exporter.getExportedClasses(),
            exporter.getRelationshipDatas(),
            exporter.getExportedPackages(),
            exporter.getExportedNary(),
            exporter.getNotes()
        );
    }
}
