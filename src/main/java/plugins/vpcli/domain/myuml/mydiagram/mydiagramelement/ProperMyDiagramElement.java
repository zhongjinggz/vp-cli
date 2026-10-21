package plugins.vpcli.domain.myuml.mydiagram.mydiagramelement;

import plugins.vpcli.domain.myuml.mycommon.MyElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// 除了 MyDiagram 之外，其他 MyDiagramElement 的父类
// 由于 MyDiagram 既是MyElement， 也是 MyDiagramElement, 加上Java 的单继承限制，因此无法继承 BaseMyDiagramElement
// "Proper" 借用了 "Proper Subset"(真子集)中的命名
public class ProperMyDiagramElement implements MyDiagramElement {
    protected String id;
    protected DiagramElementType diagramElementType;
    protected MyElement modelElement;
    protected MyDiagramElement parentDiagramElement;
    protected List<ProperMyDiagramElement> subShapes = new ArrayList<>();
    protected List<ProperMyDiagramElement> subEdges = new ArrayList<>();

    public ProperMyDiagramElement(
        String id,
        DiagramElementType diagramElementType,
        MyElement modelElement,
        MyDiagramElement parentDiagramElement
    ) {
        this.id = id;
        this.diagramElementType = diagramElementType;
        this.modelElement = modelElement;
        this.parentDiagramElement = parentDiagramElement;
        this.parentDiagramElement.addSubDiagramElement(this);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public DiagramElementType getDiagramElementType() {
        return diagramElementType;
    }

    @Override
    public MyElement getModelElement() {
        return modelElement;
    }

    public boolean isShape() {
        return this.diagramElementType.isShape();
    }

    public boolean isEdge() {
        return this.diagramElementType.isEdge();
    }

    public MyDiagramElement getParentDiagramElement() {
        return parentDiagramElement;
    }

    @Override
    public void addSubDiagramElement(ProperMyDiagramElement subDiagramElement) {
        if (subDiagramElement.isShape()) {
            this.subShapes.add(subDiagramElement);
        } else if (subDiagramElement.isEdge()) {
            this.subEdges.add(subDiagramElement);
        }
    }

    @Override
    public List<ProperMyDiagramElement> getSubShapes() {
        return Collections.unmodifiableList(this.subShapes);
    }
}
