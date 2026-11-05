package plugins.vpcli.domain.outputmodel.pumlexporter;

import java.util.HashMap;
import java.util.Map;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plugins.vpcli.domain.outputmodel.pumlexporter.pumlclass.PumlAssociationExporter;
import plugins.vpcli.domain.outputmodel.pumlexporter.pumlclass.PumlClassExporter;
import plugins.vpcli.domain.outputmodel.pumlexporter.pumlclass.PumlGeneralizationExporter;
import plugins.vpcli.domain.outputmodel.pumlexporter.pumldependency.*;
import plugins.vpcli.domain.outputmodel.pumlexporter.pumlnamespace.PumlImportExporter;
import plugins.vpcli.domain.outputmodel.pumlexporter.pumlnamespace.PumlMergeExporter;
import plugins.vpcli.domain.outputmodel.pumlexporter.pumlnamespace.PumlModelExporter;
import plugins.vpcli.domain.outputmodel.pumlexporter.pumlnamespace.PumlPackageExporter;
import plugins.vpcli.domain.outputmodel.pumlexporter.pumlnote.PumlAnchorExporter;
import plugins.vpcli.domain.outputmodel.pumlexporter.pumlnote.PumlNoteExporter;
import plugins.vpcli.domain.outputmodel.pumlexporter.pumlusecase.PumlUseCaseExporter;

@Singleton
public class PumlElementExporterRepository {
    private static Logger log = LoggerFactory.getLogger(PumlElementExporterRepository.class);
    private final Map<String, PumlElementExporter> shapeTypeExporters = new HashMap<>();
    private final PumlUnimplementedExporter unimplementedExporter;

    @Inject
    public PumlElementExporterRepository(
        PumlUnimplementedExporter unimplementedExporter,
        PumlAssociationExporter associationExporter,
        PumlClassExporter classExporter,
        PumlContainmentExporter containmentExporter,
        PumlPackageExporter packageExporter,
        PumlModelExporter modelExporter,
        PumlModelRelationshipContainerExporter modelRelationshipContainerExporter,
        PumlUseCaseExporter useCaseExporter,
        PumlDependencyExporter dependencyExporter,
        PumlGeneralizationExporter generalizationExporter,
        PumlRealizationExporter realizationExporter,
        PumlNoteExporter noteExporter,
        PumlAnchorExporter anchorExporter,
        PumlUsageExporter usageExporter,
        PumlBindingDependencyExporter bindingDependencyExporter,
        PumlPermissionExporter permissionExporter,
        PumlAccessExporter accessExporter,
        PumlImportExporter importExporter,
        PumlMergeExporter mergeExporter,
        PumlInstantiationExporter instantiationExporter,
        PumlSubstitutionExporter substitutionExporter,
        PumlAbstractionExporter abstractionExporter,
        PumlDeriveExporter deriveExporter,
        PumlRefineExporter refineExporter,
        PumlTraceExporter traceExporter
    ) {
        // special
        this.unimplementedExporter = unimplementedExporter;
        register(unimplementedExporter);
        register(modelRelationshipContainerExporter);

        // note
        register(noteExporter);
        register(anchorExporter);

        // class
        register(classExporter);
        register(associationExporter);
        register(generalizationExporter);
        register(containmentExporter);

        // package
        register(packageExporter);
        register(modelExporter);

        // usecase
        register(useCaseExporter);

        // dependency
        register(dependencyExporter);
        register(realizationExporter);
        register(usageExporter);
        register(bindingDependencyExporter);
        register(permissionExporter);
        register(accessExporter);
        register(importExporter);
        register(mergeExporter);
        register(instantiationExporter);
        register(substitutionExporter);
        register(abstractionExporter);
        register(deriveExporter);
        register(refineExporter);
        register(traceExporter);

    }


    private void register(PumlElementExporter exporter) {
        shapeTypeExporters.put(exporter.getShapeType(), exporter); // 单一来源：@Override getShapeType()
        exporter.setExporterRepository(this); // to findByShapeType the bi-direction dependency
    }


    /**
     * （警告并跳过），与迁移前经 MyElementFactory 归一化为 UNIMPLEMENTED_ELEMENT 等价。
     */
    public PumlElementExporter findByShapeType(String shapeType) {
        PumlElementExporter exporter = this.shapeTypeExporters.get(shapeType);
        if (exporter != null) {
            return exporter;
        }

        log.warn("Unimplemented shape type: {}", shapeType);
        return unimplementedExporter;
    }
}
