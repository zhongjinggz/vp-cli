package plugins.plantUML;

import org.checkerframework.checker.nullness.qual.NonNull;
import plugins.plantUML.export.*;
import plugins.plantUML.export.writers.*;

public class WriterFactory {
    @NonNull
    public ActivityUMLWriter createActivityUMLWriter(ActivityDiagramExporter exporter) {
        return new ActivityUMLWriter(
            exporter.getNotes(),
            exporter.getRootNode()
        );
    }

    @NonNull
    public StateUMLWriter createStateUMLWriter(StateDiagramExporter exporter) {
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
    public UseCaseWriter createUseCaseWriter(UseCaseDiagramExporter exporter) {
        return new UseCaseWriter(
            exporter.getExportedUseCases(),
            exporter.getExportedRelationships(),
            exporter.getExportedPackages(),
            exporter.getExportedActors(),
            exporter.getNotes()
        );
    }

    @NonNull
    public SequenceUMLWriter createSequenceUMLWriter(SequenceDiagramExporter exporter) {
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
    public ComponentDeploymentUMLWriter createComponentDeploymentUMLWriter(ComponentDeploymentDiagramExporter exporter) {
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
    public ClassUMLWriter createClassUMLWriter(ClassDiagramExporter exporter) {
        return new ClassUMLWriter(
            exporter.getExportedClasses(),
            exporter.getRelationshipDatas(),
            exporter.getExportedPackages(),
            exporter.getExportedNary(),
            exporter.getNotes()
        );
    }
}
