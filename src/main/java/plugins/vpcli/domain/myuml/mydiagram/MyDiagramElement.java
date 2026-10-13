package plugins.vpcli.domain.myuml.mydiagram;

import plugins.vpcli.domain.myuml.mycommon.MyElement;

import java.util.List;

public interface MyDiagramElement {
    public String getId();

    public DiagramElementType getDiagramElementType();

    public MyElement getModelElement();

    void addSubDiagramElement(ProperMyDiagramElement properMyDiagramElement);

    List<ProperMyDiagramElement> getShapes();
}
