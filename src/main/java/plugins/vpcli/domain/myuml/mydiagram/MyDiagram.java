package plugins.vpcli.domain.myuml.mydiagram;

import org.jetbrains.annotations.NotNull;
import plugins.vpcli.domain.myuml.mycommon.ElementType;
import plugins.vpcli.domain.myuml.mycommon.MyElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static plugins.vpcli.domain.myuml.mydiagram.DiagramElementType.DIAGRAM;

// 由于 Java 单继承的约束，MyDiagram 和 ProperMyDiagramElement 的代码略有重复，未来考虑是否要抽取
public class MyDiagram extends MyElement implements MyDiagramElement {
    private String name;
    private final DiagramType diagramType;
    private MyElement parentModelElement;
    protected List<ProperMyDiagramElement> subShapes = new ArrayList<>();
    protected List<ProperMyDiagramElement> subEdges = new ArrayList<>();


    public MyDiagram(
        @NotNull MyElement parentElement,
        @NotNull String id,
        @NotNull String name,
//        @NotNull MyElementType myElementType,
        @NotNull String description,
        @NotNull DiagramType diagramType
    ) {
        super(parentElement, id, name, ElementType.DIAGRAM
//            , myElementType
            , description);
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
