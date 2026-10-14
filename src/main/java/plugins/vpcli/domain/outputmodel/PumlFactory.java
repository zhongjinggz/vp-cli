package plugins.vpcli.domain.outputmodel;

import plugins.vpcli.domain.myuml.mydiagram.MyDiagram;

public class PumlFactory {
    private final PumlDiagramCreator createPumlDiagram;

    public PumlFactory(PumlDiagramCreator pumlDiagramCreator) {
        this.createPumlDiagram = pumlDiagramCreator;
    }

    public PumlDiagram of(MyDiagram myDiagram) {
        var result = createPumlDiagram.of(myDiagram.getName(), myDiagram.getType());
        for (var aShape : myDiagram.getSubShapes()) {
            var myElement = aShape.getModelElement();
            var exportMyElement = myElement.getElementType().getPumlElementExporter();
            exportMyElement.to(result, aShape);
        }
        return result;
    }
}
