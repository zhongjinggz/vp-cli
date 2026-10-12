package plugins.vpcli.domain.myuml.mydiagram;

import plugins.vpcli.domain.myuml.mycommon.MyElement;

public interface MyDiagramElement {
    public String getId();

    public DiagramElementType getDiagramElementType();

    public MyElement getModelElement();

    void addSubDiagramElement(ProperMyDiagramElement properMyDiagramElement);
}
