package plugins.vpcli.domain.outputmodel.pumelementexporter;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.outputmodel.NameDirection;
import plugins.vpcli.domain.outputmodel.PumlDiagram;
import plugins.vpcli.domain.outputmodel.PumlDiagram.LineStyle;
import plugins.vpcli.domain.outputmodel.PumlEdgeEnd;
import plugins.vpcli.domain.outputmodel.PumlEdgeEnd.ShapeKind;

@Singleton
public class PumlAssociationExporter extends PumlElementExporter {
    @Inject
    public PumlAssociationExporter() {
    }

    @Override
    public String getShapeType() {
        return IShapeTypeConstants.SHAPE_TYPE_ASSOCIATION;
    }

    /**
     * 步 A5：直接读 VP 原生关联。连接线（IDiagramElement）的 getModelElement() 即 IAssociation；
     * 两端经 getFromEnd()/getToEnd() 得 IAssociationEnd（继承 IModelElement，
     * 自带 multiplicity/navigable/aggregation 与 role 名称），端分类器取 getModelElement()。
     */
    @Override
    public void to(PumlDiagram result, IDiagramElement vpDiagramElement) {
        IAssociation association = (IAssociation) vpDiagramElement.getModelElement();

        IAssociationEnd fromEnd = (IAssociationEnd) association.getFromEnd();
        IAssociationEnd toEnd = (IAssociationEnd) association.getToEnd();

        // 别名读端分类器（而非端点本身），与 myuml 侧 getFromEnd().getElement() 语义一致。
        String fromAlias = getPumlAlias(fromEnd.getModelElement());
        String toAlias   = getPumlAlias(toEnd.getModelElement());

        result.writeEdge(
            PumlEdgeEnd.of(fromAlias, normalizeMultiplicity(fromEnd.getMultiplicity()), fromEnd.getName(), fromShapeForVP(fromEnd)),
            PumlEdgeEnd.of(toAlias, normalizeMultiplicity(toEnd.getMultiplicity()), toEnd.getName(), toShapeForVP(toEnd)),
            LineStyle.SOLID,
            association.getName(),
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
