package plugins.vpcli.domain.vpstruct;

import com.vp.plugin.model.IModelElement;

public class VPStructElementFactory {
    VPStructElement from(IModelElement vpElement) {
        return new VPStructElement(vpElement);
    }
}
