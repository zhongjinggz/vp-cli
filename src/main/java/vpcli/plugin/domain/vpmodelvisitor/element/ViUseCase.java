package vpcli.plugin.domain.vpmodelvisitor.element;

import com.vp.plugin.model.IModelElement;
import vpcli.plugin.domain.vpmodelvisitor.visitor.ViElementVisitor;

public class ViUseCase extends ViModelElement {
    public ViUseCase(IModelElement vpElement, ViElementFactory viElementFactory) {
        super(vpElement, viElementFactory);
    }

    @Override
    public void accept(ViElementVisitor visitor) {
        visitor.visit(this);
    }
}
