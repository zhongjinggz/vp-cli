package plugins.vpcli.domain.vpvisitor.vpdiagramvisitor.element;

import com.vp.plugin.diagram.IConnectorUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeUIModel;

public class ViDiagramElementFactory {
    public ViDiagramElement from(IDiagramElement shape) {
        return new ViDiagramElement(shape, this);
    }

}
