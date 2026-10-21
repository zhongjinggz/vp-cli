package plugins.vpcli.domain.outputmodel.pumelementexporter;

import plugins.vpcli.domain.myuml.mydiagram.mydiagramelement.ProperMyDiagramElement;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.outputmodel.PumlDiagram;

@Singleton
public class PumlClassExporter extends PumlElementExporter {
    @Inject
    public PumlClassExporter() {
    }

    public void to(PumlDiagram result, ProperMyDiagramElement myDiagramElement) {
        var myElement = myDiagramElement.getModelElement();
        result.beginBlock("class \"" + myElement.getName() + "\" as " + myElement.getPumlAlias());
        for (var aSubShape : myDiagramElement.getSubShapes()) {
            var exporter = pumlElementExporterFactory.of(aSubShape.getModelElement().getElementType());
            exporter.to(result, aSubShape);
        }
        result.endBlock();

    }
}
