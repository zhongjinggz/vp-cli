package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element;

import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModel;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.factory.IModelElementFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;

public class ViElementFactory {
    private MyElementFactory myElementFactory;

    public ViElementFactory(MyElementFactory myElementFactory) {
        this.myElementFactory = myElementFactory;
    }


    public ViElement from(IModelElement element) {
        switch (element.getModelType()) {
            case IModelElementFactory.MODEL_TYPE_PACKAGE:
                return new ViPackage(element, this, this.myElementFactory);
            case IModelElementFactory.MODEL_TYPE_MODEL:
                return new ViModel(element, this, this.myElementFactory);
            case IModelElementFactory.MODEL_TYPE_CLASS:
                return new ViClass(element, this, this.myElementFactory);
            case IModelElementFactory.MODEL_TYPE_USE_CASE:
                return new ViUseCase(element, this, this.myElementFactory);
            default:
                throw new IllegalArgumentException("Wrong element type:" + element.getModelType().toString());
        }
    }

    ViElement from(IDiagramUIModel diagram) {
        return new ViDiagramAsElement(diagram);
    }
}
