package vpcli.plugin.domain.outputmodel.pumlexporter;

import java.util.HashMap;
import java.util.Map;

import com.vp.plugin.diagram.IDiagramElement;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vpcli.plugin.domain.outputmodel.pumlexporter.pumlclass.*;
import vpcli.plugin.domain.outputmodel.pumlexporter.pumldependency.*;
import vpcli.plugin.domain.outputmodel.pumlexporter.pumlnamespace.PumlImportExporter;
import vpcli.plugin.domain.outputmodel.pumlexporter.pumlnamespace.PumlMergeExporter;
import vpcli.plugin.domain.outputmodel.pumlexporter.pumlnamespace.PumlModelExporter;
import vpcli.plugin.domain.outputmodel.pumlexporter.pumlnamespace.PumlPackageExporter;
import vpcli.plugin.domain.outputmodel.pumlexporter.pumlnote.PumlAnchorExporter;
import vpcli.plugin.domain.outputmodel.pumlexporter.pumlnote.PumlNoteExporter;
import vpcli.plugin.domain.outputmodel.pumlexporter.pumlusecase.PumlUseCaseExporter;

import static vpcli.plugin.domain.vpmodel.DiagramElements.isInterfaceBall;

@Singleton
public class PumlElementExporterRepository {
    private static final Logger log = LoggerFactory.getLogger(PumlElementExporterRepository.class);
    private final Map<String, PumlElementExporter> exporters = new HashMap<>();
    private final PumlUnimplementedExporter unimplementedExporter;

    @Inject
    public PumlElementExporterRepository(
        // special
        PumlUnimplementedExporter unimplementedExporter,
        PumlModelRelationshipContainerExporter modelRelationshipContainerExporter,

        // note
        PumlNoteExporter noteExporter,
        PumlAnchorExporter anchorExporter,

        // class
        PumlClassExporter classExporter,
        PumlAssociationExporter associationExporter,
        PumlAssociationClassExporter associationClassExporter,
        PumlNAryExporter naryAssociationExporter,
        PumlGeneralizationExporter generalizationExporter,
        PumlContainmentExporter containmentExporter,
        PumlInterfaceBallExporter interfaceBallExporter,

        // package
        PumlPackageExporter packageExporter,
        PumlModelExporter modelExporter,

        // usecase
        PumlUseCaseExporter useCaseExporter,

        // dependency
        PumlDependencyExporter dependencyExporter,
        PumlRealizationExporter realizationExporter,
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
        register(associationClassExporter);
        register(naryAssociationExporter);
        register(generalizationExporter);
        register(containmentExporter);
        register(interfaceBallExporter); // Visual Paradigm use SHAPE_TYPE_CLASS also for interface ball, so, use pseudo shape type here.

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
        exporters.put(exporter.getShapeType(), exporter); // 单一来源：@Override getShapeType()
        exporter.setExporterRepository(this); // to findBy the bi-direction dependency
    }


    /**
     * （警告并跳过），与迁移前经 MyElementFactory 归一化为 UNIMPLEMENTED_ELEMENT 等价。
     */
    public PumlElementExporter findBy(IDiagramElement vpDiagramElement) {
        if (isInterfaceBall(vpDiagramElement)) {
            return this.exporters.get(PumlInterfaceBallExporter.SHAPE_TYPE);
        }

        var theShapeType = vpDiagramElement.getShapeType();

        PumlElementExporter exporter = this.exporters.get(theShapeType);
        if (exporter != null) {
            return exporter;
        }

        log.warn("Unimplemented shape type: {}", theShapeType);
        return unimplementedExporter;
    }

}
