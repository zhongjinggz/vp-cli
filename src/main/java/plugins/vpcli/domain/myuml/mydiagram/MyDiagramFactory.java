package plugins.vpcli.domain.myuml.mydiagram;

import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.IShapeUIModel;
import org.slf4j.Logger;
import plugins.vpcli.domain.myuml.myelement.*;

import static com.vp.plugin.diagram.IShapeTypeConstants.SHAPE_TYPE_ASSOCIATION;
import static org.slf4j.LoggerFactory.getLogger;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.myuml.mydiagram.mydiagramelement.MyDiagramElementFactory;

@Singleton
public class MyDiagramFactory {
    private final Logger logger;
    private final MyDiagramElementFactory buildMyDiagramElement;

    @Inject
    public MyDiagramFactory(MyDiagramElementFactory myDiagramElementFactory) {
        this(myDiagramElementFactory,
            getLogger(MyDiagramFactory.class)
        );

    }

    public MyDiagramFactory(
        MyDiagramElementFactory myDiagramElementFactory
        , Logger logger
    ) {

        this.buildMyDiagramElement = myDiagramElementFactory;
        this.logger = logger;
    }

    public MyDiagram of(IDiagramUIModel vpDiagram, MyElement parentElement) {
        MyDiagram result = creatMyDiagram(vpDiagram, parentElement);

        var vpShape = vpDiagram.toShapeUIModelArray();
        if (vpShape != null) {
            for (var aShape : vpShape) {
                // parent is null means it is top level
                // TODO how to imporve the efficiency against filtering all shapes?
                if (aShape.getParent() == null) {
                    debug(aShape);
                    buildMyDiagramElement.ofShape(aShape, result);
                }
            }
        }

        var vpConnectors = vpDiagram.toConnectorUIModelArray();
        if (vpConnectors != null) {
            for (var aConnector : vpConnectors) {
                // 只处理关联连接线，其余类型（依赖/泛化等）留给后续步骤
                if (SHAPE_TYPE_ASSOCIATION.equals(aConnector.getShapeType())) {
                    buildMyDiagramElement.ofConnector(aConnector, result);
                }
            }
        }

        return result;
    }

    MyDiagram creatMyDiagram(
        IDiagramUIModel vpDiagram,
        MyElement parentElement
    ) {
        var description = vpDiagram.getDocumentation() == null ? "" : vpDiagram.getDocumentation();
        return new MyDiagram(
            parentElement,
            vpDiagram.getId(),
            vpDiagram.getName(),
            description,
            DiagramType.of(vpDiagram.getType())
        );
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
