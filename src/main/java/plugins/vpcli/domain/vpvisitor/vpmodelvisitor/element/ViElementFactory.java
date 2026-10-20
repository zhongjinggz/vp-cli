package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element;

import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactoryFactory;

import static com.vp.plugin.model.factory.IModelElementFactory.*;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class ViElementFactory {
    protected final MyElementFactoryFactory myElementFactoryFactory;

    @Inject
    public ViElementFactory(MyElementFactoryFactory myElementFactoryFactory) {
        this.myElementFactoryFactory = myElementFactoryFactory;
    }

    public ViElement from(IModelElement element) {
        var myElementFactory = myElementFactoryFactory.of(element.getModelType());
        switch (element.getModelType()) {
            case MODEL_TYPE_PACKAGE:
                return new ViPackage(element, this, myElementFactory);
            case MODEL_TYPE_MODEL:
                return new ViModel(element, this, myElementFactory);
            case MODEL_TYPE_CLASS:
                return new ViClass(element, this, myElementFactory);
            case MODEL_TYPE_USE_CASE:
                return new ViUseCase(element, this, myElementFactory);
            default:
                throw new IllegalArgumentException("Wrong element type:" + element.getModelType());
        }
    }

    ViElement from(IDiagramUIModel diagram) {
        return new ViDiagramAsElement(diagram);
    }

    protected MyElementFactory getMyElementFactory(IModelElement element) {
        return myElementFactoryFactory.of(element.getModelType());
    }
}
