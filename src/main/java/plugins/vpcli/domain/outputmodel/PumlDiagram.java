package plugins.vpcli.domain.outputmodel;

import plugins.vpcli.domain.myuml.mydiagram.DiagramType;

public class PumlDiagram {
    private String name;
    private DiagramType diagramType;

    public PumlDiagram(String name, DiagramType diagramType) {
        this.name = name;
        this.diagramType = diagramType;

    }
}
