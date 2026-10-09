package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element;

import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitor;

import java.io.IOException;

public class ViUseCase extends ViModelElement {
    public ViUseCase(IModelElement vpElement, ViElementFactory elementFactory) {
        super(vpElement, elementFactory);
    }

    @Override
    public void accept(ViElementVisitor visitor) throws IOException {
        visitor.visit(this);
    }
}
