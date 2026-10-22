package plugins.vpcli.domain.outputmodel.pumelementexporter;

import plugins.vpcli.domain.myuml.mydiagram.mydiagramelement.ProperMyDiagramElement;
import plugins.vpcli.domain.myuml.myassociation.MyAssociation;
import plugins.vpcli.domain.myuml.myassociation.MyAssociationEnd;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.outputmodel.PumlDiagram;
import plugins.vpcli.domain.outputmodel.PumlDiagram.LineStyle;
import plugins.vpcli.domain.outputmodel.PumlEdgeEnd;
import plugins.vpcli.domain.outputmodel.PumlEdgeEnd.ShapeKind;

@Singleton
public class PumlAssociationExporter extends PumlElementExporter {
    @Inject
    public PumlAssociationExporter() {
    }

    public void to(PumlDiagram result, ProperMyDiagramElement myDiagramElement) {
        var association = (MyAssociation) myDiagramElement.getModelElement();

        var fromEnd = association.getFromEnd();
        var toEnd = association.getToEnd();

        String fromAlias = getPumlAlias(fromEnd.getElement());
        String toAlias   = getPumlAlias(toEnd.getElement());

        result.writeEdge(
            PumlEdgeEnd.of(fromAlias, fromEnd.getMultiplicity(), "", fromShapeFor(fromEnd)),
            PumlEdgeEnd.of(toAlias, toEnd.getMultiplicity(), "", toShapeFor(toEnd)),
            LineStyle.SOLID);
    }

    /**
     * from 端在行的左侧（尖端朝左的符号才能使箭头/叉号贴合 from 端）。
     */
    private ShapeKind fromShapeFor(MyAssociationEnd end) {
        switch (end.getNavigable()) {
            case MyAssociationEnd.NAVIGABLE_NAVIGABLE:
                return ShapeKind.REVERSE_ARROW;
            case MyAssociationEnd.NAVIGABLE_NON_NAVIGABLE:
                return ShapeKind.CROSS;
            default:
                return ShapeKind.NONE;
        }
    }

    /**
     * to 端在行的右侧（尖端朝右的符号）。
     */
    private ShapeKind toShapeFor(MyAssociationEnd end) {
        switch (end.getNavigable()) {
            case MyAssociationEnd.NAVIGABLE_NAVIGABLE:
                return ShapeKind.ARROW;
            case MyAssociationEnd.NAVIGABLE_NON_NAVIGABLE:
                return ShapeKind.CROSS;
            default:
                return ShapeKind.NONE;
        }
    }
}
