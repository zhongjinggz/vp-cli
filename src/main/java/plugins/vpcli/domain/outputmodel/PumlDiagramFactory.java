package plugins.vpcli.domain.outputmodel;

import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.connector.IAnchorUIModel;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.outputmodel.pumelementexporter.PumlElementExporterFactory;

@Singleton
public class PumlDiagramFactory {
    private final PumlElementExporterFactory exporterFactory;

    @Inject
    public PumlDiagramFactory(
        PumlElementExporterFactory exporterFactory
    ) {
        this.exporterFactory = exporterFactory;
    }

    /**
     * 步 A13/B4：遍历直读 VP 原生图表，不再经 myuml 的 MyDiagram 集合。
     * 形状只取顶层（沿用迁移前 MyDiagramFactory 的 aShape.getParent()==null 过滤），
     * 嵌套形状由 PumlBlockExporter 沿 toChildArray() 递归渲染，故此处不重复处理；
     * 连接线滤除锚点（IAnchorUIModel）。分发一律按 VP getShapeType()，未知类型由
     * PumlElementExporterFactory 兜底到未实现 exporter（警告并跳过，行为等价）。
     */
    public PumlDiagram build(IDiagramUIModel vpDiagramElement) {
        var result = createPumlDiagram(vpDiagramElement);

        var shapes = vpDiagramElement.toShapeUIModelArray();
        if (shapes != null) {
            for (var aShape : shapes) {
                if (aShape.getParent() != null) {
                    continue;
                }
                exporterFactory.build(aShape.getShapeType()).export(result, aShape);
            }
        }

        var connectors = vpDiagramElement.toConnectorUIModelArray();
        if (connectors != null) {
            for (var aConnector : connectors) {
                if (aConnector instanceof IAnchorUIModel) {
                    continue;
                }
                exporterFactory.build(aConnector.getShapeType()).export(result, aConnector);
            }
        }

        return result;
    }

    PumlDiagram createPumlDiagram(IDiagramUIModel vpDiagramElement) {
        String name = vpDiagramElement.getName();
        return new PumlDiagram(name);
    }
}
