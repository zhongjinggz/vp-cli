package plugins.vpcli.domain.myuml.mydiagram;

import plugins.vpcli.domain.myuml.mycommon.MyElement;

public class MyShape extends ProperMyDiagramElement {
    public MyShape(
        String id,
        DiagramElementType diagramElementType,
        MyElement modelElement,
        MyDiagramElement parentDiagramElement
    ) {
        super(id, diagramElementType, modelElement, parentDiagramElement);
    }
}
