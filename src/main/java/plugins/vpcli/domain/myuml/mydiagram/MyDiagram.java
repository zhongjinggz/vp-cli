package plugins.vpcli.domain.myuml.mydiagram;

import org.jetbrains.annotations.NotNull;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyElement_old;

public class MyDiagram extends MyElement implements MyDiagramElement {
    private String name;
    private DiagramType diagramType;
    private MyElement_old parentModelElement;

    
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
        return DiagramElementType.DIAGRAM;
    }

    @Override
    public MyElement getModelElement() {
        return null;
    }

    @Override
    public MyDiagramElement getParentDiagramElement() {
        return null;
    }

    @Override
    public void addSubDiagramElement(BaseMyDiagramElement baseMyDiagramElement) {

    }
}
