package plugins.vpcli.domain.vpvisitor.mydiagramfactory;

import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.IShapeUIModel;
import org.slf4j.Logger;
import plugins.vpcli.domain.myuml.mycommon.*;
import plugins.vpcli.domain.myuml.mydiagram.DiagramType;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagram;

import static org.slf4j.LoggerFactory.getLogger;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class MyDiagramFactory {
    private final Logger logger;
    private final MyDiagramElementFactory buildMyDiagramElement;

    @Inject
    public MyDiagramFactory(
//        MyDiagramCreator myDiagramCreator,
        MyDiagramElementFactory myDiagramElementFactory
    ) {
        this(
            myDiagramElementFactory,
            getLogger(MyDiagramFactory.class));

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

                debug(aShape);

                // parent is null means it is top level
                if (aShape.getParent() == null) {
                    buildMyDiagramElement.of(aShape, result);
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
