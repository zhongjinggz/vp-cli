package plugins.vpcli.domain.outputmodel.pumelementexporter;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import plugins.vpcli.domain.myuml.mydiagram.mydiagramelement.ProperMyDiagramElement;
import plugins.vpcli.domain.myuml.myassociation.MyAssociation;
import plugins.vpcli.domain.myuml.myassociation.MyAssociationEnd;

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
     * 过渡入口（步 A1 以来的通行对）：经由 myuml 构造的 ProperMyDiagramElement 读取。
     * 步 A5 起关联改走下方 3 参 VP 版本，本方法仅保留给尚未迁移到 VP 的旧调用方。
     */
    @Override
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
            toDirection(association.getNameDirection()));
    }

    /**
     * 步 A5：直接读 VP 原生关联。连接线（IDiagramElement）的 getModelElement() 即 IAssociation；
     * 两端经 getFromEnd()/getToEnd() 得 IAssociationEnd（继承 IModelElement，
     * 自带 multiplicity/navigable/aggregation 与 role 名称），端分类器取 getModelElement()。
     */
    @Override
    public void to(PumlDiagram result, ProperMyDiagramElement myDiagramElement, IDiagramElement vpDiagramElement) {
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

    /**
     * from 端在行的左侧（尖端朝左的符号才能使箭头/叉号贴合 from 端）。
     * 聚合/组合菱形优先于导航符号——同端两者无法在 PlantUML 中共存。
     */
    private ShapeKind fromShapeFor(MyAssociationEnd end) {
        String aggregation = end.getAggregationKind();
        if (MyAssociationEnd.AGGREGATION_SHARED.equals(aggregation)) {
            return ShapeKind.DIAMOND;
        }
        if (MyAssociationEnd.AGGREGATION_COMPOSITE.equals(aggregation)) {
            return ShapeKind.SOLID_DIAMOND;
        }
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
     * 聚合/组合菱形优先于导航符号——同端两者无法在 PlantUML 中共存。
     */
    private ShapeKind toShapeFor(MyAssociationEnd end) {
        String aggregation = end.getAggregationKind();
        if (MyAssociationEnd.AGGREGATION_SHARED.equals(aggregation)) {
            return ShapeKind.DIAMOND;
        }
        if (MyAssociationEnd.AGGREGATION_COMPOSITE.equals(aggregation)) {
            return ShapeKind.SOLID_DIAMOND;
        }
        switch (end.getNavigable()) {
            case MyAssociationEnd.NAVIGABLE_NAVIGABLE:
                return ShapeKind.ARROW;
            case MyAssociationEnd.NAVIGABLE_NON_NAVIGABLE:
                return ShapeKind.CROSS;
            default:
                return ShapeKind.NONE;
        }
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

    /** 把企业级 NameDirection 映射为前端枚举（过渡路径）。 */
    private NameDirection toDirection(MyAssociation.NameDirection direction) {
        switch (direction == null ? MyAssociation.NameDirection.NONE : direction) {
            case FROM_TO:
                return NameDirection.FROM_TO;
            case TO_FROM:
                return NameDirection.TO_FROM;
            default:
                return NameDirection.NONE;
        }
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
