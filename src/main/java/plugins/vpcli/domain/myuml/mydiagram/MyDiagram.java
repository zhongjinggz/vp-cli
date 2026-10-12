package plugins.vpcli.domain.myuml.mydiagram;

import org.jetbrains.annotations.NotNull;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyElement_old;

import java.util.List;

import static plugins.vpcli.domain.myuml.mydiagram.DiagramElementType.DIAGRAM;

// 由于 Java 单继承的约束，MyDiagram 和 ProperMyDiagramElement 的代码略有重复，未来考虑是否要抽取
public class MyDiagram extends MyElement implements MyDiagramElement {
    private String name;
    private DiagramType diagramType;
    private MyElement parentModelElement;
    protected List<MyShape> subShapes;
    protected List<MyEdge> subEdges;

    
    public MyDiagram(
        @NotNull MyElement  parentElement,
        @NotNull String id ,
        @NotNull String name ,
        @NotNull String description,
        @NotNull DiagramType diagramType
    ) {
        super(parentElement, id, name, description);
        this.diagramType = diagramType;
    }
    
    public DiagramType getType() {
        return diagramType;
    }

    @Override
    public String getId() {
        return super.getId();
    }

    @Override
    public DiagramElementType getDiagramElementType() {
        return DIAGRAM;
    }

    @Override
    // Diagram 本身就是 UML 的 一种元素，因此指向自身
    public MyElement getModelElement() {
        return this;
    }

    @Override
    public void addSubDiagramElement(ProperMyDiagramElement subDiagramElement) {
        if (subDiagramElement instanceof MyShape) {
            this.subShapes.add((MyShape) subDiagramElement);
        } else if (subDiagramElement instanceof MyEdge) {
            this.subEdges.add((MyEdge) subDiagramElement);
        }

    }
}
