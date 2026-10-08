package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element;

import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;

public class ViElementFactory {
    public ViElement from(IModelElement element) {
        return new ViModelElement(element, this);
    }

    ViElement from (IDiagramUIModel diagram) {
        return new ViDiagramAsElement(diagram);
    }
}
