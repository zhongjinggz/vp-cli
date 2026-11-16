package vpcli.plugin.domain.vpmodelvisitor.element;

import com.vp.plugin.model.IModelElement;
import vpcli.plugin.domain.vpmodelvisitor.visitor.Visitor;

public class ViUseCase extends ViLeafModelElement {
    public ViUseCase(IModelElement vpElement) {
        super(vpElement);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
