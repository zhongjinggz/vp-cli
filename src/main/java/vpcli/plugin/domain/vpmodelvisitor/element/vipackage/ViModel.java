package vpcli.plugin.domain.vpmodelvisitor.element.vipackage;

import com.vp.plugin.model.IModelElement;
import vpcli.plugin.domain.vpmodelvisitor.element.fundation.ViElementFactory;
import vpcli.plugin.domain.vpmodelvisitor.visitor.Visitor;

public class ViModel extends ViPackage {
    public ViModel(IModelElement vpElement,
                   ViElementFactory viElementFactory
    ) {
        super(vpElement, viElementFactory);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
