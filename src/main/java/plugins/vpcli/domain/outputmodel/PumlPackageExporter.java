package plugins.vpcli.domain.outputmodel;

import plugins.vpcli.domain.myuml.mydiagram.ProperMyDiagramElement;

public class PumlPackageExporter implements PumlElementExporter {

    @Override
    public void to(PumlDiagram result, ProperMyDiagramElement myDiagramElement) {
        var myElement = myDiagramElement.getModelElement();
        result.beginBlock("namespace " + myElement.getName());
        for (var aSubShape : myDiagramElement.getSubShapes()) {
            var exporter = aSubShape.getModelElement().getElementType().getPumlElementExporter();
            exporter.to(result, aSubShape);
        }
        result.endBlock();

    }
}
