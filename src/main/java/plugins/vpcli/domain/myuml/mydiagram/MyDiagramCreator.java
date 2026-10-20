package plugins.vpcli.domain.myuml.mydiagram;

import com.vp.plugin.diagram.IDiagramUIModel;
import plugins.vpcli.domain.myuml.mycommon.MyElement;

public class MyDiagramCreator {
    public MyDiagram from(MyElement parentElement, IDiagramUIModel vpDiagram
//        , MyElementType myElementType
    ) {
        String desc = vpDiagram.getDocumentation() == null ? "" : vpDiagram.getDocumentation();
        return new MyDiagram(
            parentElement,
            vpDiagram.getId(),
            vpDiagram.getName(),
//            myElementType,
//            vpDiagram.getDocumentation(),
            desc,
            DiagramType.of(vpDiagram.getType()));
    }
}
