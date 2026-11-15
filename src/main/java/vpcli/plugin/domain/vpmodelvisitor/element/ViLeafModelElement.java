package vpcli.plugin.domain.vpmodelvisitor.element;

import com.vp.plugin.model.IModelElement;

public abstract class ViLeafModelElement extends ViModelElement implements ViLeafElement{

    public ViLeafModelElement(IModelElement vpElement) {
        super(vpElement);
    }

}