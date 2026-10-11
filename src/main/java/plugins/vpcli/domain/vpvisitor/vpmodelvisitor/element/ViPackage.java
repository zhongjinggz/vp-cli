package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element;

import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyElement_old;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;
import plugins.vpcli.domain.myuml.mypackage.MyPackage;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitor;

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
    public MyPackage toMyElement(MyElement parent) {
        return myElementFactory.createPackage(
            parent,
            this.vpElement.getId(),
            this.vpElement.getName(),
            this.vpElement.getDescription()
        );

    }
}
