package vpcli.plugin.domain.vpmodelvisitor.element.fundation;

import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;

import static com.vp.plugin.model.factory.IModelElementFactory.*;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vpcli.plugin.domain.vpmodelvisitor.element.viclassifier.ViClass;
import vpcli.plugin.domain.vpmodelvisitor.element.viclassifier.ViNary;
import vpcli.plugin.domain.vpmodelvisitor.element.viclassifier.ViUseCase;
import vpcli.plugin.domain.vpmodelvisitor.element.vidiagram.ViDiagram;
import vpcli.plugin.domain.vpmodelvisitor.element.vipackage.ViModel;
import vpcli.plugin.domain.vpmodelvisitor.element.vipackage.ViPackage;

@Singleton
public class ViElementFactory {
    Logger log = LoggerFactory.getLogger(ViElementFactory.class);

    @Inject
    public ViElementFactory() {
    }

    public ViElement build(IModelElement element) {
        switch (element.getModelType()) {
            case MODEL_TYPE_PACKAGE:
                return new ViPackage(element, this);
            case MODEL_TYPE_MODEL:
                return new ViModel(element, this);
            case MODEL_TYPE_CLASS:
                return new ViClass(element);
            case MODEL_TYPE_USE_CASE:
                return new ViUseCase(element);
            case MODEL_TYPE_NARY:
                return new ViNary(element);
            default:
                // throw new IllegalArgumentException("Wrong element type:" + element.getModelType());
                log.debug("building unimplemented element with type {}" , element.getModelType());
                return new ViUnimplemented(element);
        }
    }

    public ViElement build(IDiagramUIModel diagram) {
        return new ViDiagram(diagram);
    }

}
