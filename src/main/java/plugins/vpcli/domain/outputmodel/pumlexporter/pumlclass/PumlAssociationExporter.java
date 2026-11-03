package plugins.vpcli.domain.outputmodel.pumlexporter.pumlclass;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.outputmodel.pumldiagram.NameDirection;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlDiagram;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlDiagram.LineStyle;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlEdgeEnd;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlEdgeEnd.ShapeKind;
import plugins.vpcli.domain.outputmodel.pumlexporter.PumlElementExporter;

@Singleton
public class PumlAssociationExporter extends PumlElementExporter {
    @Inject
    public PumlAssociationExporter() {
    }

    @Override
    public String getShapeType() {
        return IShapeTypeConstants.SHAPE_TYPE_ASSOCIATION;
    }

    @Override
    public void export(PumlDiagram result, IDiagramElement vpDiagramElement) {
        IAssociation association = (IAssociation) vpDiagramElement.getModelElement();

        IAssociationEnd fromEnd = (IAssociationEnd) association.getFromEnd();
        IAssociationEnd toEnd = (IAssociationEnd) association.getToEnd();

        String fromAlias = getPumlAlias(fromEnd.getModelElement());
        String toAlias   = getPumlAlias(toEnd.getModelElement());

        result.writeEdge(
            PumlEdgeEnd.of(fromAlias, normalizeMultiplicity(fromEnd.getMultiplicity()), fromEnd.getName(), fromShapeForVP(fromEnd)),
            PumlEdgeEnd.of(toAlias, normalizeMultiplicity(toEnd.getMultiplicity()), toEnd.getName(), toShapeForVP(toEnd)),
            LineStyle.SOLID,
            buildEdgeLabel(association.getName(), association),
            toDirectionVP(association.getDirection()));
    }

    /** 步 A5：VP 原生版本复用的形状映射，经 IAssociationEnd 读取。 */
    private ShapeKind shapeFor(IAssociationEnd end, boolean isFromEnd) {
        String aggregation = end.getAggregationKind();
        if (IAssociationEnd.AGGREGATION_KIND_shared.equals(aggregation)) {
            return ShapeKind.DIAMOND;
        }
        if (IAssociationEnd.AGGREGATION_KIND_composite.equals(aggregation)) {
            return ShapeKind.SOLID_DIAMOND;
        }
        switch (end.getNavigable()) {
            case IAssociationEnd.NAVIGABLE_NAVIGABLE:
                return isFromEnd ? ShapeKind.REVERSE_ARROW : ShapeKind.ARROW;
            case IAssociationEnd.NAVIGABLE_NON_NAVIGABLE:
                return ShapeKind.CROSS;
            default:
                return ShapeKind.NONE;
        }
    }

    private ShapeKind fromShapeForVP(IAssociationEnd end) {
        return shapeFor(end, true);
    }

    private ShapeKind toShapeForVP(IAssociationEnd end) {
        return shapeFor(end, false);
    }

    /** 与 MyAssociationEnd.setMultiplicity 一致：null 或 "Unspecified" 归空串。 */
    private String normalizeMultiplicity(String multiplicity) {
        if (multiplicity == null || IAssociationEnd.MULTIPLICITY_UNSPECIFIED.equals(multiplicity)) {
            return "";
        }
        return multiplicity;
    }

    /** 把 VP 方向常量映射为前端枚举，两者之外归为 NONE（与 MyAssociationFactory 逻辑一致）。 */
    private NameDirection toDirectionVP(int direction) {
        if (direction == IAssociation.DIRECTION_FROM_TO) {
            return NameDirection.FROM_TO;
        }
        if (direction == IAssociation.DIRECTION_TO_FROM) {
            return NameDirection.TO_FROM;
        }
        return NameDirection.NONE;
    }
}
