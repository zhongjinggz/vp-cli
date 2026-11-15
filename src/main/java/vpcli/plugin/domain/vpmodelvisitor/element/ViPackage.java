package vpcli.plugin.domain.vpmodelvisitor.element;

import com.vp.plugin.model.IModelElement;
import vpcli.plugin.domain.vpmodelvisitor.visitor.Visitor;

public class ViPackage extends ViCompositeModelElement {

    public ViPackage(IModelElement vpElement,
                     ViElementFactory viElementFactory
    ) {
        super(vpElement, viElementFactory);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
