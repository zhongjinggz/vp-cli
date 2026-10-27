package plugins.vpcli.domain.vpmodelvisitor.element;

import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;

import static com.vp.plugin.model.factory.IModelElementFactory.*;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class ViElementFactory {

    @Inject
    public ViElementFactory() {
    }

    public ViElement from(IModelElement element) {
        switch (element.getModelType()) {
            case MODEL_TYPE_PACKAGE:
                return new ViPackage(element, this);
            case MODEL_TYPE_MODEL:
                return new ViModel(element, this);
            case MODEL_TYPE_CLASS:
                return new ViClass(element, this);
            case MODEL_TYPE_USE_CASE:
                return new ViUseCase(element, this);
            default:
                throw new IllegalArgumentException("Wrong element type:" + element.getModelType());
        }
    }

    ViElement from(IDiagramUIModel diagram) {
        return new ViDiagramAsElement(diagram);
    }

}
