package plugins.vpcli.domain.myuml.mydiagram;

import com.vp.plugin.diagram.IDiagramUIModel;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.myelementtype.MyElementType;
import plugins.vpcli.domain.myuml.mycommon.myelementtype.MyElementTypes;

public class MyDiagramCreator {
    public MyDiagram from(MyElement parentElement, IDiagramUIModel vpDiagram, MyElementType myElementType) {
        return new MyDiagram(
            parentElement,
            vpDiagram.getId(),
            vpDiagram.getName(),
            myElementType,
            vpDiagram.getDocumentation(),
            DiagramType.of(vpDiagram.getType()));
    }
}
