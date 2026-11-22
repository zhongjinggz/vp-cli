package vpcli.plugin.domain.vpmodelvisitor.element.viclassifier;

import com.vp.plugin.model.IModelElement;
import vpcli.plugin.domain.vpmodelvisitor.element.fundation.ViLeafModelElement;
import vpcli.plugin.domain.vpmodelvisitor.visitor.Visitor;

public class ViClass extends ViLeafModelElement {
    public ViClass(IModelElement vpElement) {
        super(vpElement);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
