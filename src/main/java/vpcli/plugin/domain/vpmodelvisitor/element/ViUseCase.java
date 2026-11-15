package vpcli.plugin.domain.vpmodelvisitor.element;

import com.vp.plugin.model.IModelElement;
import vpcli.plugin.domain.vpmodelvisitor.visitor.Visitor;

public class ViUseCase extends ViCompositeModelElement {
    public ViUseCase(IModelElement vpElement, ViElementFactory viElementFactory) {
        super(vpElement, viElementFactory);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
