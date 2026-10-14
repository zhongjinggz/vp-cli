package plugins.vpcli.domain.vpvisitor.mydiagramfactory;

import com.vp.plugin.diagram.IDiagramUIModel;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.myelementtype.MyElementTypes;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagram;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagramCreator;

public class MyDiagramFactory {
    private final MyDiagramElementFactory buildMyDiagramElement;
    private final MyDiagramCreator createMyDiagram;
    private final MyElementTypes myElementTypes;

    public MyDiagramFactory(
        MyDiagramCreator myDiagramCreator
        , MyDiagramElementFactory myDiagramElementFactory, MyElementTypes myElementTypes) {

        this.buildMyDiagramElement = myDiagramElementFactory;
        this.createMyDiagram = myDiagramCreator;
        this.myElementTypes = myElementTypes;
    }

    public MyDiagram of(IDiagramUIModel vpDiagram, MyElement parentElement) {
        MyDiagram result = createMyDiagram.from(parentElement, vpDiagram, myElementTypes.DIAGRAM);

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
