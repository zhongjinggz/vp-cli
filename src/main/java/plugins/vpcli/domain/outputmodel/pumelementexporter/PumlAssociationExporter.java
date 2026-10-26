package plugins.vpcli.domain.outputmodel.pumelementexporter;

import plugins.vpcli.domain.myuml.mydiagram.mydiagramelement.ProperMyDiagramElement;
import plugins.vpcli.domain.myuml.myassociation.MyAssociation;
import plugins.vpcli.domain.myuml.myassociation.MyProperty;

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
            PumlEdgeEnd.of(fromAlias, fromEnd.getMultiplicity(), fromEnd.getName(), fromShapeFor(fromEnd)),
            PumlEdgeEnd.of(toAlias, toEnd.getMultiplicity(), toEnd.getName(), toShapeFor(toEnd)),
            LineStyle.SOLID,
            association.getName(),
            association.getNameDirection());
    }

    /**
     * from 端在行的左侧（尖端朝左的符号才能使箭头/叉号贴合 from 端）。
     * 聚合/组合菱形优先于导航符号——同端两者无法在 PlantUML 中共存。
     */
    private ShapeKind fromShapeFor(MyProperty end) {
        String aggregation = end.getAggregationKind();
        if (MyProperty.AGGREGATION_SHARED.equals(aggregation)) {
            return ShapeKind.DIAMOND;
        }
        if (MyProperty.AGGREGATION_COMPOSITE.equals(aggregation)) {
            return ShapeKind.SOLID_DIAMOND;
        }
        switch (end.getNavigable()) {
            case MyProperty.NAVIGABLE_NAVIGABLE:
                return ShapeKind.REVERSE_ARROW;
            case MyProperty.NAVIGABLE_NON_NAVIGABLE:
                return ShapeKind.CROSS;
            default:
                return ShapeKind.NONE;
        }
    }

    /**
     * to 端在行的右侧（尖端朝右的符号）。
     * 聚合/组合菱形优先于导航符号——同端两者无法在 PlantUML 中共存。
     */
    private ShapeKind toShapeFor(MyProperty end) {
        String aggregation = end.getAggregationKind();
        if (MyProperty.AGGREGATION_SHARED.equals(aggregation)) {
            return ShapeKind.DIAMOND;
        }
        if (MyProperty.AGGREGATION_COMPOSITE.equals(aggregation)) {
            return ShapeKind.SOLID_DIAMOND;
        }
        switch (end.getNavigable()) {
            case MyProperty.NAVIGABLE_NAVIGABLE:
                return ShapeKind.ARROW;
            case MyProperty.NAVIGABLE_NON_NAVIGABLE:
                return ShapeKind.CROSS;
            default:
                return ShapeKind.NONE;
        }
    }
}
