package plugins.vpcli.domain.outputmodel.pumelementexporter;

import java.util.HashMap;
import java.util.Map;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class PumlElementExporterFactory {
    private final Map<String, PumlElementExporter> shapeTypeExporters = new HashMap<>();
    private final PumlUnimplementedExporter unimplementedExporter;

    @Inject
    public PumlElementExporterFactory(
        PumlAssociationExporter associationExporter,
        PumlClassExporter classExporter,
        PumlPackageExporter packageExporter,
        PumlModelExporter modelExporter,
        PumlModelRelationshipContainerExporter modelRelationshipContainerExporter,
        PumlUseCaseExporter useCaseExporter,
        PumlDependencyExporter dependencyExporter,
        PumlGeneralizationExporter generalizationExporter,
        PumlUnimplementedExporter unimplementedExporter
    ) {
        this.unimplementedExporter = unimplementedExporter;
        register(associationExporter);
        register(classExporter);
        register(packageExporter);
        register(modelExporter);
        register(modelRelationshipContainerExporter);
        register(useCaseExporter);
        register(dependencyExporter);
        register(generalizationExporter);
        register(unimplementedExporter);
    }


    private void register(PumlElementExporter exporter) {
        shapeTypeExporters.put(exporter.getShapeType(), exporter); // 单一来源：@Override getShapeType()
        exporter.setExporterFactory(this); // to build the bi-direction dependency
    }


    /**
     * （警告并跳过），与迁移前经 MyElementFactory 归一化为 UNIMPLEMENTED_ELEMENT 等价。
     */
    public PumlElementExporter build(String shapeType) {
        PumlElementExporter exporter = this.shapeTypeExporters.get(shapeType);
        return exporter != null ? exporter : unimplementedExporter;
    }
}
