package vpcli.plugin.domain.vpmodelvisitor.element;

import com.vp.plugin.diagram.IDiagramUIModel;
import vpcli.plugin.domain.vpmodelvisitor.ViElementType;
import vpcli.plugin.domain.vpmodelvisitor.visitor.ViElementVisitor;

import java.io.IOException;
import java.util.List;

public class ViDiagramAsElement extends ViElement {
    private final IDiagramUIModel diagram;

    public ViDiagramAsElement(IDiagramUIModel diagram) {
        this.diagram = diagram;
    }

    @Override
    public void accept(ViElementVisitor visitor) throws IOException {
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

    @Override
    public List<ViElement> getChildren(List<ViElementType> myElementTypes) {
        return List.of();
    }

    public IDiagramUIModel getVPDiagramUIModel() {
        return this.diagram;
    }
}
