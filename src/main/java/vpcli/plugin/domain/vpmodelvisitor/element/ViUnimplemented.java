package vpcli.plugin.domain.vpmodelvisitor.element;

import com.vp.plugin.model.IModelElement;
import vpcli.plugin.domain.vpmodelvisitor.visitor.Visitor;

public class ViUnimplemented extends ViLeafModelElement {
    public ViUnimplemented(IModelElement vpElement) {
        super(vpElement);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
