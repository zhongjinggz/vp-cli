package plugins.vpcli.domain.vpstruct;

import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;

public class VPStructElementFactory {
    VPStructElement from(IModelElement element) {
        return new VPModelElement(element, this);
    }

    VPStructElement from (IDiagramUIModel diagram) {
        return new VPDiagram(diagram);
    }
}
