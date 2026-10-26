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
    private final Map<String, PumlElementExporter> shapeTypeExporters = new HashMap<>();

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
        register(MyElementType.ASSOCIATION, associationExporter);
        register(MyElementType.CLASS, classExporter);
        register(MyElementType.PACKAGE, packageExporter);
        register(MyElementType.MODEL, modelExporter);
        register(MyElementType.RELATIONSHIPS, modelRelationshipContainerExporter);
        register(MyElementType.USE_CASE, useCaseExporter);
        register(MyElementType.DEPENDENCY, dependencyExporter);
        register(MyElementType.GENERALIZATION, generalizationExporter);
        register(MyElementType.UNIMPLEMENTED_ELEMENT, unimplementedExporter);
    }


    private void register(MyElementType myElementType, PumlElementExporter exporter) {
        exporters.put(myElementType, exporter);
        shapeTypeExporters.put(exporter.getShapeType(), exporter); // 单一来源：@Override getShapeType()
        exporter.setExporterFactory(this); // to build the bi-direction dependency
    }

    public PumlElementExporter of(MyElementType myElementType) {
        if (this.exporters.containsKey(myElementType)) {
            return this.exporters.get(myElementType);
        } else {
            throw new BugException(String.format("Element type %s not found", myElementType));
        }
    }

    /**
     * 步 A3：以 VP 形状类型字符串分发（渐近式入口，供将来直读 VP 原生模型时使用）。
     * 缺类型抛 {@link BugException}。
     */
    public PumlElementExporter of(String shapeType) {
        if (this.shapeTypeExporters.containsKey(shapeType)) {
            return this.shapeTypeExporters.get(shapeType);
        } else {
            throw new BugException(String.format("Shape type %s not found", shapeType));
        }
    }
}
