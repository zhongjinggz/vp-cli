package plugins.vpcli.domain.vpmodelvisitor.element;

import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.myelement.MyElement;
import plugins.vpcli.domain.myuml.myelement.MyElementFactory;
import plugins.vpcli.domain.myuml.mypackage.MyPackage;
import plugins.vpcli.domain.vpmodelvisitor.visitor.ViElementVisitor;

import java.io.IOException;

public class ViPackage extends ViModelElement {

    public ViPackage(IModelElement vpElement,
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
    public MyPackage toMyElementUnder(MyElement parent) {
//        return (MyPackage) myElementFactory.create(parent, vpElement);
        return (MyPackage) myElementFactory.build(vpElement);

    }
}
