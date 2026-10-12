package plugins.vpcli.domain.myuml.mydiagram;

import plugins.vpcli.domain.myuml.mycommon.MyElement;

public class MyEdge extends ProperMyDiagramElement {
    public MyEdge(String id, DiagramElementType diagramElementType, MyElement modelElement, MyDiagramElement parentDiagramElement) {
        super(id, diagramElementType, modelElement, parentDiagramElement);
    }
}
