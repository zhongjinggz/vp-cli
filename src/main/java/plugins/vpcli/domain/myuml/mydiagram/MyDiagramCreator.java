package plugins.vpcli.domain.myuml.mydiagram;

import com.vp.plugin.diagram.IDiagramUIModel;
import plugins.vpcli.domain.myuml.mycommon.MyElement;

public class MyDiagramCreator {
    public MyDiagram create(MyElement parentElement, IDiagramUIModel vpDiagram) {
        return new MyDiagram(
            parentElement,
            vpDiagram.getId(),
            vpDiagram.getName(),
            vpDiagram.getDocumentation(),
            DiagramType.of(vpDiagram.getType()));
    }
}
