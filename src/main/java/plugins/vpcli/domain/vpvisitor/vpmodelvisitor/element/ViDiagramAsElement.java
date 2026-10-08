package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element;

import com.vp.plugin.diagram.IDiagramUIModel;
import plugins.vpcli.domain.myuml.mycommon.ElementType;

import java.util.List;

public class ViDiagramAsElement extends ViElement {
    private final IDiagramUIModel diagram;

    public ViDiagramAsElement(IDiagramUIModel diagram) {
        this.diagram = diagram;
    }
    public String getName() {
        return diagram.getName();
    }
    
    public ElementType getType() {
        return ElementType.DIAGRAM;
    }

    @Override
    public List<ViElement> getChildren(List<ElementType> elementTypes) {
        return List.of();
    }

    public IDiagramUIModel getVPDiagramUIModel() {
        return this.diagram;
    }
}
