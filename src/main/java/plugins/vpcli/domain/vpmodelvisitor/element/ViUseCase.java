package plugins.vpcli.domain.vpmodelvisitor.element;

import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.vpmodelvisitor.visitor.ViElementVisitor;

import java.io.IOException;

public class ViUseCase extends ViModelElement {
    public ViUseCase(IModelElement vpElement, ViElementFactory viElementFactory) {
        super(vpElement, viElementFactory);
    }

    @Override
    public void accept(ViElementVisitor visitor) throws IOException {
        visitor.visit(this);
    }
}
