package plugins.vpcli.domain.outputmodel;

import plugins.vpcli.domain.myuml.mydiagram.MyDiagram;

public class PumlDiagramFactory {
    private final PumlElementExporterFactory exporterFactory;

    public PumlDiagramFactory(
//        PumlDiagramCreator pumlDiagramCreator,
        PumlElementExporterFactory exporterFactory
    ) {
        this.exporterFactory = exporterFactory;
    }

    public PumlDiagram of(MyDiagram myDiagram) {
        var result = createPumlDiagram(myDiagram);
        for (var aShape : myDiagram.getSubShapes()) {
            var myElement = aShape.getModelElement();
            var exportMyElement = exporterFactory.of(myElement.getElementType());
            exportMyElement.to(result, aShape);
        }
        return result;
    }

    PumlDiagram createPumlDiagram(MyDiagram myDiagram) {
        String name = myDiagram.getName();
        return new PumlDiagram(name, myDiagram.getType());
    }
}
