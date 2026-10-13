package plugins.vpcli.domain.outputmodel;

import plugins.vpcli.domain.myuml.mydiagram.DiagramType;

public class PumlDiagramCreator {
    public PumlDiagram of(String name, DiagramType diagramType) {
        return new PumlDiagram(name, diagramType);
    }
}
