package plugins.vpcli.domain.vpvisitor.mydiagramfactory;

import com.vp.plugin.diagram.IDiagramElement;
import org.jetbrains.annotations.NotNull;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mydiagram.DiagramElementType;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagramElement;
import plugins.vpcli.domain.myuml.mydiagram.ProperMyDiagramElement;

public class MyDiagramElementCreator {
    public @NotNull ProperMyDiagramElement of(
        IDiagramElement vpDiagramElement,
        MyDiagramElement parentDiagramElement,
        MyElement myElement
    ) {
        return new ProperMyDiagramElement(
            vpDiagramElement.getId(),
            DiagramElementType.of(vpDiagramElement.getShapeType()),
            myElement,
            parentDiagramElement
        );
    }
}
