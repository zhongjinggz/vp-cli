package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element;

import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;
import plugins.vpcli.domain.myuml.mypackage.MyModel;
import plugins.vpcli.domain.myuml.mypackage.MyPackage;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitor;

import java.io.IOException;

public class ViModel extends ViPackage {
    public ViModel(IModelElement vpElement,
                   ViElementFactory viElementFactory,
                   MyElementFactory myElementFactory
    ) {
        super(vpElement, viElementFactory, myElementFactory);
    }

    @Override
    public void accept(ViElementVisitor visitor) throws IOException {
        visitor.visit(this);
    }

    @Override
    public MyModel toMyElement(MyElement parent) {
//        return myElementFactory.createModel(parent, vpElement);
        return (MyModel) myElementFactory.create(parent, vpElement);

    }
}
