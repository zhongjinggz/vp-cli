package plugins.vpcli.domain.myuml.mydiagram;

import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyElement_old;

public interface MyDiagramElement {
    public String getId();
    public DiagramElementType getDiagramElementType();
    public MyElement getModelElement();
    public MyDiagramElement getParentDiagramElement();

    void addSubDiagramElement(BaseMyDiagramElement baseMyDiagramElement);
}
