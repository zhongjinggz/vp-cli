package plugins.vpcli.domain.vpvisitor.mydiagramfactory;

import com.vp.plugin.diagram.IConnectorUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactoryFactory;
import plugins.vpcli.domain.myuml.mydiagram.*;

public class MyDiagramElementFactory {
    private final MyElementFactoryFactory myElementFactoryFactory;
    private final MyDiagramElementCreator createMyDiagramElement;

    public MyDiagramElementFactory(
        MyElementFactoryFactory myElementFactoryFactory,
        MyDiagramElementCreator myDiagramElementCreator
    ) {
        this.myElementFactoryFactory = myElementFactoryFactory;
        this.createMyDiagramElement = myDiagramElementCreator;
    }

    public MyDiagramElement of(IShapeUIModel vpShape,
                               MyDiagramElement parentDiagramElement) {
        IModelElement vpElement = vpShape.getModelElement();

        MyElementFactory buildMyElement = myElementFactoryFactory.of(
            vpElement.getModelType());
        var myElement = buildMyElement.of(vpElement);

        var result = createMyDiagramElement.of(
            vpShape,
            parentDiagramElement,
            myElement
        );

        var subShapes = vpShape.toChildArray();
        for (var aShape : subShapes) {
            this.of(aShape, result);
        }
        return result;
    }

    public MyDiagramElement of(IConnectorUIModel vpConnector,
                               MyDiagramElement parentDiagramElement) {
        IModelElement vpElement = vpConnector.getModelElement();
        MyElementFactory buildMyElement = myElementFactoryFactory.of(
            vpElement.getModelType());
        var myElement = buildMyElement.of(vpElement);

        return createMyDiagramElement.of(
            vpConnector,
            parentDiagramElement,
            myElement
        );

    }
}
