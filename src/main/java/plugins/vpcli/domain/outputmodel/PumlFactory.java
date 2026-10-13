package plugins.vpcli.domain.outputmodel;

import plugins.vpcli.domain.myuml.mydiagram.MyDiagram;

public class PumlFactory {
    private final PumlDiagramCreator createPumlDiagram;
    private final PumlElementExporterFactory pumlElementExporterFactory;

    public PumlFactory(PumlDiagramCreator pumlDiagramCreator, PumlElementExporterFactory pumlElementExporterFactory) {
        this.createPumlDiagram = pumlDiagramCreator;
        this.pumlElementExporterFactory = pumlElementExporterFactory;
    }

    public PumlDiagram of(MyDiagram myDiagram) {
        var result = createPumlDiagram.of(myDiagram.getName(), myDiagram.getType());
        for (var aShape : myDiagram.getShapes()) {
            var myElement = aShape.getModelElement();
            var exporter = pumlElementExporterFactory.getExporter(myElement.getElementType());
//            exporter.to(result, myElement); TO BE Continue
        }
        return result;
    }
}
