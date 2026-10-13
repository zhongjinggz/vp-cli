package plugins.vpcli.domain.vpvisitor.mydiagramfactory;

import com.vp.plugin.diagram.IConnectorUIModel;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.IShapeUIModel;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagram;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagramCreator;

public class MyDiagramFactory {
    private final MyDiagramElementFactory buildMyDiagramElement;
    private MyDiagramCreator createMyDiagram;

    public MyDiagramFactory(
        MyDiagramCreator myDiagramCreator
        , MyDiagramElementFactory myDiagramElementFactory) {

        this.buildMyDiagramElement = myDiagramElementFactory;
        this.createMyDiagram = myDiagramCreator;
    }

    public MyDiagram from(MyElement parentElement, IDiagramUIModel vpDiagram) {
        MyDiagram result = createMyDiagram.from(parentElement, vpDiagram);

        var vpDiagramElements = vpDiagram.toDiagramElementArray();
        if (vpDiagramElements != null) {
            for (var aVPDiagramElement : vpDiagramElements) {
                buildMyDiagramElement.of(aVPDiagramElement, result);
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
}
