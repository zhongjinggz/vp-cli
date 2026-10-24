package plugins.vpcli.domain.vpmodelvisitor.element;

import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.myelement.MyElement;
import plugins.vpcli.domain.myuml.myelement.MyElementFactory;
import plugins.vpcli.domain.myuml.mymodel.MyModel;
import plugins.vpcli.domain.vpmodelvisitor.visitor.ViElementVisitor;

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
    public MyModel toMyElementUnder(MyElement parent) {
//        return (MyModel) myElementFactory.create(parent, vpElement);
        return (MyModel) myElementFactory.of(vpElement);

    }
}
