package plugins.vpcli.domain.outputmodel.pumelementexporter;

import plugins.vpcli.domain.myuml_old.myelement_old.MyElementType;

import java.util.HashMap;
import java.util.Map;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.util.BugException;

@Singleton
public class PumlElementExporterFactory {
    private final Map<MyElementType, PumlElementExporter> oldExporters = new HashMap<>();
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
        oldExporters.put(myElementType, exporter);
        shapeTypeExporters.put(exporter.getShapeType(), exporter); // 单一来源：@Override getShapeType()
        exporter.setExporterFactory(this); // to build the bi-direction dependency
    }

//    public PumlElementExporter of(MyElementType myElementType) {
//        if (this.oldExporters.containsKey(myElementType)) {
//            return this.oldExporters.get(myElementType);
//        } else {
//            throw new BugException(String.format("Element type %s not found", myElementType));
//        }
//    }

    /**
     * 步 A3/A13：以 VP 形状类型字符串分发。未知类型兜底到未实现 exporter
     * （警告并跳过），与迁移前经 MyElementFactory 归一化为 UNIMPLEMENTED_ELEMENT 等价。
     */
    public PumlElementExporter of(String shapeType) {
        PumlElementExporter exporter = this.shapeTypeExporters.get(shapeType);
        return exporter != null ? exporter : unimplementedExporter;
    }
}
