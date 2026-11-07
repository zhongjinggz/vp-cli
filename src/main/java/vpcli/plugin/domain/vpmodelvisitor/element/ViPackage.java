package vpcli.plugin.domain.vpmodelvisitor.element;

import com.vp.plugin.model.IModelElement;
import vpcli.plugin.domain.vpmodelvisitor.visitor.ViElementVisitor;

public class ViPackage extends ViModelElement {

    public ViPackage(IModelElement vpElement,
                     ViElementFactory viElementFactory
    ) {
        super(vpElement, viElementFactory);
    }

    @Override
    public void accept(ViElementVisitor visitor) {
        visitor.visit(this);
    }
}
