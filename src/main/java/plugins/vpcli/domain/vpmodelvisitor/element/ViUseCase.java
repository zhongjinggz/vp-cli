package plugins.vpcli.domain.vpmodelvisitor.element;

import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;
import plugins.vpcli.domain.myuml.mypackage.MyPackage;
import plugins.vpcli.domain.vpmodelvisitor.visitor.ViElementVisitor;

import java.io.IOException;

public class ViUseCase extends ViModelElement {
    public ViUseCase(IModelElement vpElement, ViElementFactory viElementFactory, MyElementFactory myElementFactory) {
        super(vpElement, viElementFactory, myElementFactory);
    }

    @Override
    public MyPackage toMyElementUnder(MyElement parent) {
        return null;
    }

    @Override
    public void accept(ViElementVisitor visitor) throws IOException {
        visitor.visit(this);
    }
}
