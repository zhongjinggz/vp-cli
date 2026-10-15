package plugins.vpcli.domain.outputmodel;

import plugins.vpcli.domain.myuml.mydiagram.ProperMyDiagramElement;

public class PumlAssociationExporter implements PumlElementExporter {
    public void to(PumlDiagram result, ProperMyDiagramElement myDiagramElement) {
        var myElement = myDiagramElement.getModelElement();
        result.writeTerminalElement(" -- ");

    }
}
