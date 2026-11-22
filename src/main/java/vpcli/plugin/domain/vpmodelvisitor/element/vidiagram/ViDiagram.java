package vpcli.plugin.domain.vpmodelvisitor.element.vidiagram;

import com.vp.plugin.diagram.IDiagramUIModel;
import vpcli.plugin.domain.vpmodelvisitor.element.fundation.ViElementType;
import vpcli.plugin.domain.vpmodelvisitor.element.fundation.ViLeafElement;
import vpcli.plugin.domain.vpmodelvisitor.visitor.Visitor;

public class ViDiagram implements ViLeafElement {
    private final IDiagramUIModel diagram;

    public ViDiagram(IDiagramUIModel diagram) {
        this.diagram = diagram;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getName() {
        return diagram.getName();
    }

    @Override
    public ViElementType getType() {
        return ViElementType.DIAGRAM;
    }

    public IDiagramUIModel getVPDiagramUIModel() {
        return this.diagram;
    }
}
