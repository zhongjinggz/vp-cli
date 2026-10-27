package plugins.vpcli.domain.myuml.mydiagram.mydiagramelement;

import plugins.vpcli.domain.myuml_old.myelement_old.MyElement_old2;

import java.util.List;

public interface MyDiagramElement {
    public String getId();

    public DiagramElementType getDiagramElementType();

    public MyElement_old2 getModelElement();

    void addSubDiagramElement(ProperMyDiagramElement properMyDiagramElement);

    List<ProperMyDiagramElement> getSubShapes();

    List<ProperMyDiagramElement> getSubEdges();
}
