package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element;

import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;

public class VPElementFactory {
    public VPElement from(IModelElement element) {
        return new VPModelElement(element, this);
    }

    VPElement from (IDiagramUIModel diagram) {
        return new VPDiagramAsElement(diagram);
    }
}
