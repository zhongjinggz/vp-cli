package plugins.vpcli.domain.myuml.mydiagram;

import plugins.vpcli.domain.myuml.mycommon.MyElement;

import java.util.List;

// 除了 MyDiagram 之外，其他 MyDiagramElement 的父类
// 由于 MyDiagram 既是MyElement， 也是 MyDiagramElement, 加上Java 的单继承限制，因此无法继承 BaseMyDiagramElement
public class BaseMyDiagramElement implements MyDiagramElement {
    protected String id;
    protected DiagramElementType diagramElementType;
    protected MyElement modelElement;
    protected MyDiagramElement parentDiagramElement;
    protected List<MyShape> subShapes;
    protected List<MyEdge> subEdges;

    public BaseMyDiagramElement(
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

    @Override
    public MyDiagramElement getParentDiagramElement() {
        return parentDiagramElement;
    }

    @Override
    public void addSubDiagramElement(BaseMyDiagramElement subDiagramElement) {
        if (subDiagramElement instanceof MyShape) {
            this.subShapes.add((MyShape) subDiagramElement);
        } else if (subDiagramElement instanceof MyEdge) {
            this.subEdges.add((MyEdge) subDiagramElement);
        }
    }
}
