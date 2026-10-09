package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element;

import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModel;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.factory.IModelElementFactory;

public class ViElementFactory {
    public ViElement from(IModelElement element) {
        switch (element.getModelType()) {
            case IModelElementFactory.MODEL_TYPE_PACKAGE:
                return new ViPackage(element, this);
            case IModelElementFactory.MODEL_TYPE_MODEL:
                return new ViModel(element, this);
            case IModelElementFactory.MODEL_TYPE_CLASS:
                return new ViClass(element, this);
            case IModelElementFactory.MODEL_TYPE_USE_CASE:
                return new ViUseCase(element, this);
            default:
                throw new IllegalArgumentException("Wrong element type:" + element.getModelType().toString());
        }
    }

    ViElement from(IDiagramUIModel diagram) {
        return new ViDiagramAsElement(diagram);
    }
}
