package plugins.vpcli.domain.outputmodel;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagram;

import java.util.HashMap;
import java.util.Map;

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

    public PumlDiagram of(MyDiagram myDiagram, IDiagramUIModel vpDiagramElement) {
        var result = createPumlDiagram(myDiagram);
        var vpElementsById = collectVpDiagramElements(vpDiagramElement);
        for (var aShape : myDiagram.getSubShapes()) {
            var myElement = aShape.getModelElement();
            var exportMyElement = exporterFactory.of(myElement.getElementType());
            exportMyElement.to(result, aShape, vpElementsById.get(aShape.getId()));
        }
        for (var aEdge : myDiagram.getSubEdges()) {
            var myElement = aEdge.getModelElement();
            var exportMyElement = exporterFactory.of(myElement.getElementType());
            exportMyElement.to(result, aEdge, vpElementsById.get(aEdge.getId()));
        }
        return result;
    }

    /**
     * 以 VP 原生图表（IDiagramUIModel）为突破口，收集图表内全部图形/连接线元素
     * 的「VP 元素 id → VP 图形元素(IDiagramElement)」映射。MyDiagram 中每个
     * ProperMyDiagramElement 的 id 与对应 VP 元素的 id 一致，因此下游据此分发 VP 原生模型。
     */
    private Map<String, IDiagramElement> collectVpDiagramElements(IDiagramUIModel vpDiagram) {
        Map<String, IDiagramElement> map = new HashMap<>();
        var shapes = vpDiagram.toShapeUIModelArray();
        if (shapes != null) {
            collectVpDiagramElements(map, shapes);
        }
        var connectors = vpDiagram.toConnectorUIModelArray();
        if (connectors != null) {
            collectVpDiagramElements(map, connectors);
        }
        return map;
    }

    private void collectVpDiagramElements(Map<String, IDiagramElement> map, IDiagramElement[] elements) {
        for (var element : elements) {
            map.put(element.getId(), element);
        }
    }

    PumlDiagram createPumlDiagram(MyDiagram myDiagram) {
        String name = myDiagram.getName();
        return new PumlDiagram(name, myDiagram.getType());
    }
}
