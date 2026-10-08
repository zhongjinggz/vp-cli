package plugins.vpcli.domain.vpvisitor.vpdiagramvisitor.element;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeUIModel;
import plugins.vpcli.domain.vpvisitor.vpdiagramvisitor.visitor.ViDiagramElementVisitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ViDiagramElement {
    protected IDiagramElement vpDiagramElement;
    private ViDiagramElementFactory createElement;

    public ViDiagramElement(IDiagramElement vpDiagramElement, ViDiagramElementFactory diagramElementFactory) {
        this.vpDiagramElement = vpDiagramElement;
        this.createElement = diagramElementFactory;
    }

    public void accept(ViDiagramElementVisitor visitor) throws IOException {
        visitor.visit(this);
    }

    public String getName() {
        var modelElement = vpDiagramElement.getModelElement();
        return modelElement != null ? modelElement.getName() : vpDiagramElement.getId();
    }

    public DiagramElementType getType() {
        return DiagramElementType.of(vpDiagramElement.getShapeType());
    }

    public List<ViDiagramElement> getChildren() {
        List<ViDiagramElement> result = new ArrayList<>();
        var children = vpDiagramElement.toChildArray();
        if (children != null) {
            for (IShapeUIModel aChild : children) {
                result.add(createElement.from(aChild));
            }
        }
        return result;
    }

    public boolean typeIs(DiagramElementType elementType) {
        return getType().equals(elementType);
    }
}
