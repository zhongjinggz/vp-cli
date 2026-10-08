package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element;

import com.vp.plugin.diagram.IDiagramUIModel;
import plugins.vpcli.domain.myuml.mycommon.ElementType;

import java.util.List;

import static plugins.vpcli.domain.myuml.mycommon.ElementType.Kind.DIAGRAM;

public class ViDiagramAsElement extends ViElement {
    private final IDiagramUIModel diagram;

    public ViDiagramAsElement(IDiagramUIModel diagram) {
        this.diagram = diagram;
    }
    public String getName() {
        return diagram.getName();
    }
    
    public ElementType getType() {
        return ElementType.of(DIAGRAM, diagram.getType());
    }

    @Override
    public List<ViElement> getChildren(List<ElementType> elementTypes) {
        return List.of();
    }

    public IDiagramUIModel getVPDiagramUIModel() {
        return this.diagram;
    }
}
