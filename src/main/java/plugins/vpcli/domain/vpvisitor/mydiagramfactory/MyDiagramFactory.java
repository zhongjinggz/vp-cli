package plugins.vpcli.domain.vpvisitor.mydiagramfactory;

import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.IShapeUIModel;
import org.slf4j.Logger;
import plugins.vpcli.domain.myuml.mycommon.*;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagram;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagramCreator;

import static org.slf4j.LoggerFactory.getLogger;

public class MyDiagramFactory {
    private final Logger logger;
    private final MyDiagramElementFactory buildMyDiagramElement;
    private final MyDiagramCreator createMyDiagram;
//    private final MyElementTypes myElementTypes;

    public MyDiagramFactory(
        MyElementPool myElementPool
        , MyDiagramCreator myDiagramCreator
        , MyDiagramElementFactory myDiagramElementFactory
//        , MyElementTypes myElementTypes
//        , MyElementTypeRepository myElementTypeRepository
    ) {
        this(myElementPool
            , myDiagramCreator,
            myDiagramElementFactory,
//            myElementTypes,
            getLogger(MyDiagramFactory.class));

    }

    public MyDiagramFactory(
        MyElementPool myElementPool
        , MyDiagramCreator myDiagramCreator
        , MyDiagramElementFactory myDiagramElementFactory
//        , MyElementTypes myElementTypes
        , Logger logger
    ) {

        this.buildMyDiagramElement = myDiagramElementFactory;
        this.createMyDiagram = myDiagramCreator;
//        this.myElementTypes = myElementTypes;
        this.logger = logger;
    }

    public MyDiagram of(IDiagramUIModel vpDiagram, MyElement parentElement) {
        MyDiagram result = createMyDiagram.from(parentElement, vpDiagram
//            , myElementTypes.DIAGRAM
        );

        var vpShape = vpDiagram.toShapeUIModelArray();
        if (vpShape != null) {
            for (var aShape : vpShape) {

                debug(aShape);

                // parent is null means it is top level
                if (aShape.getParent() == null) {
                    buildMyDiagramElement.of(aShape, result);
                }
            }
        }

//        var vpConnectors = vpDiagram.toConnectorUIModelArray();
//        if (vpConnectors != null) {
//            for (IConnectorUIModel aConnector : vpConnectors) {
//                buildMyDiagramElement.of(aConnector, result);
//            }
//        }


        return result;
    }

    private void debug(IShapeUIModel aShape) {
        var parentId = aShape.getParent() == null ? "no-parent" : aShape.getParent().getId();
        logger.debug("Creating top level MyDiagram Element: type={} name={} id={} parent={} ",
            aShape.getShapeType(),
            aShape.getModelElement().getName(),
            aShape.getId(),
            parentId
        );
    }
}
