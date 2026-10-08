package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element;

import com.vp.plugin.diagram.IDiagramUIModel;
import plugins.vpcli.domain.myuml.mycommon.ElementType;

import java.util.List;

import static plugins.vpcli.domain.myuml.mycommon.ElementType.Kind.DIAGRAM;

public class VPDiagramAsElement extends VPElement {
    private final IDiagramUIModel diagram;

    public VPDiagramAsElement(IDiagramUIModel diagram) {
        this.diagram = diagram;
    }
    public String getName() {
        return diagram.getName();
    }
    
    public ElementType getType() {
        return ElementType.of(DIAGRAM, diagram.getType());
    }

    @Override
    public List<VPElement> getChildren(List<ElementType> elementTypes) {
        return List.of();
    }

    public IDiagramUIModel getVPDiagramUIModel() {
        return this.diagram;
    }
}
