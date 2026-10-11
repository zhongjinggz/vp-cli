package plugins.vpcli.domain.myuml.mydiagram;

import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyElement_old;

public class MyShape extends BaseMyDiagramElement {
    public MyShape(
        String id,
        DiagramElementType diagramElementType,
        MyElement modelElement,
        MyDiagramElement parentDiagramElement
    ) {
        super(id, diagramElementType, modelElement, parentDiagramElement);
    }
}
