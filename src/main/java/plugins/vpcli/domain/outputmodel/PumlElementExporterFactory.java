package plugins.vpcli.domain.outputmodel;

import plugins.vpcli.domain.myuml.mycommon.ElementType;

import java.util.HashMap;
import java.util.Map;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class PumlElementExporterFactory {
    private final Map<ElementType, PumlElementExporter> exporters = new HashMap<>();

    @Inject
    public PumlElementExporterFactory(
        PumlAssociationExporter associationExporter,
        PumlClassExporter classExporter,
        PumlPackageExporter packageExporter,
        PumlModelExporter modelExporter,
        PumlModelRelationshipContainerExporter modelRelationshipContainerExporter,
        PumlUseCaseExporter useCaseExporter
    ) {
        register(ElementType.ASSOCIATION, associationExporter);
        register(ElementType.CLASS, classExporter);
        register(ElementType.PACKAGE, packageExporter);
        register(ElementType.MODEL, modelExporter);
        register(ElementType.RELATIONSHIPS, modelRelationshipContainerExporter);
        register(ElementType.USE_CASE, useCaseExporter);
    }


    private void register(ElementType elementType, PumlElementExporter exporter) {
        exporters.put(elementType, exporter);
        exporter.setExporterFactory(this); // to build the bi-direction dependency
    }

    public PumlElementExporter of(ElementType elementType) {
        if (this.exporters.containsKey(elementType)) {
            return this.exporters.get(elementType);
        } else {
            throw new IllegalArgumentException(String.format("Element type %s not found", elementType));
        }
    }
}
