package plugins.vpcli.domain.outputmodel;

import plugins.vpcli.domain.myuml.mydiagram.MyDiagram;

public class PumlDiagramFactory {
    private final PumlDiagramCreator createPumlDiagram;
    private final PumlElementExporterFactory exporterFactory;

    public PumlDiagramFactory(PumlDiagramCreator pumlDiagramCreator, PumlElementExporterFactory exporterFactory) {
        this.createPumlDiagram = pumlDiagramCreator;
        this.exporterFactory = exporterFactory;
    }

    public PumlDiagram of(MyDiagram myDiagram) {
        var result = createPumlDiagram.of(myDiagram.getName(), myDiagram.getType());
        for (var aShape : myDiagram.getSubShapes()) {
            var myElement = aShape.getModelElement();
//            var exportMyElement = myElement.getMyElementType().getPumlElementExporter();
            var exportMyElement = exporterFactory.of(myElement.getElementType());
            exportMyElement.to(result, aShape);
        }
        return result;
    }
}
