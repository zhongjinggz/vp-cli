package plugins.vpcli.domain.vpstruct;

import com.vp.plugin.diagram.IDiagramUIModel;

import java.util.List;

import static plugins.vpcli.domain.vpstruct.ElementType.Kind.DIAGRAM;

public class VPDiagram extends VPStructElement {
    private final IDiagramUIModel diagram;

    public VPDiagram(IDiagramUIModel diagram) {
        this.diagram = diagram;
    }
    public String getName() {
        return diagram.getName();
    }
    
    public ElementType getType() {
        return ElementType.of(DIAGRAM, diagram.getType());
    }

    @Override
    public List<VPStructElement> getChildren(List<ElementType> elementTypes) {
        return List.of();
    }

    public IDiagramUIModel getVPDiagramUIModel() {
        return this.diagram;
    }
}
