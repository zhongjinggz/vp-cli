package vpcli.plugin.domain.vpmodelvisitor.element;

import com.vp.plugin.diagram.IDiagramUIModel;
import vpcli.plugin.domain.vpmodelvisitor.ViElementType;
import vpcli.plugin.domain.vpmodelvisitor.visitor.Visitor;

import java.io.IOException;
import java.util.List;

public class ViDiagramAsElement implements ViLeafElement {
    private final IDiagramUIModel diagram;

    public ViDiagramAsElement(IDiagramUIModel diagram) {
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
