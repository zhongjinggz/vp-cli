package plugins.vpcli.domain.outputmodel.pumelementexporter;

import plugins.vpcli.domain.myuml.myelement.MyElementType;

import java.util.HashMap;
import java.util.Map;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.util.BugException;

@Singleton
public class PumlElementExporterFactory {
    private final Map<MyElementType, PumlElementExporter> exporters = new HashMap<>();

    @Inject
    public PumlElementExporterFactory(
        PumlAssociationExporter associationExporter,
        PumlClassExporter classExporter,
        PumlPackageExporter packageExporter,
        PumlModelExporter modelExporter,
        PumlModelRelationshipContainerExporter modelRelationshipContainerExporter,
        PumlUseCaseExporter useCaseExporter,
        PumlDependencyExporter dependencyExporter,
        PumlUnimplementedExporter unimplementedExporter
    ) {
        register(MyElementType.ASSOCIATION, associationExporter);
        register(MyElementType.CLASS, classExporter);
        register(MyElementType.PACKAGE, packageExporter);
        register(MyElementType.MODEL, modelExporter);
        register(MyElementType.RELATIONSHIPS, modelRelationshipContainerExporter);
        register(MyElementType.USE_CASE, useCaseExporter);
        register(MyElementType.DEPENDENCY, dependencyExporter);
        register(MyElementType.UNIMPLEMENTED_ELEMENT, unimplementedExporter);
    }


    private void register(MyElementType myElementType, PumlElementExporter exporter) {
        exporters.put(myElementType, exporter);
        exporter.setExporterFactory(this); // to build the bi-direction dependency
    }

    public PumlElementExporter of(MyElementType myElementType) {
        if (this.exporters.containsKey(myElementType)) {
            return this.exporters.get(myElementType);
        } else {
            throw new BugException(String.format("Element type %s not found", myElementType));
        }
    }
}
