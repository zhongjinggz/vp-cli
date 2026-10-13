package plugins.vpcli.domain.outputmodel;

import plugins.vpcli.domain.myuml.mydiagram.MyDiagram;

public class PumlFactory {
    private PumlDiagramCreator createPumlDiagram;

    public PumlFactory(PumlDiagramCreator pumlDiagramCreator) {
        this.createPumlDiagram = pumlDiagramCreator;
    }

    public PumlDiagram of(MyDiagram myDiagram) {
        var result = createPumlDiagram.of(myDiagram.getName(), myDiagram.getType());
    }
}
