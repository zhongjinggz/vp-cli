package plugins.vpcli.domain.outputmodel.pumlexporter.pumlclass;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.vp.plugin.diagram.IConnectorUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.diagram.shape.IQualifierUIModel;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IQualifier;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlDiagram;

/**
 * 针对 {@link PumlAssociationExporter} 通过公开入口 {@code export()} 覆盖
 * {@code formatAssociation}（第 38-62 行）各分支：角色/多重度标签、阅读方向命名、
 * 多重度归一化与聚合/导航形状渲染。
 */
class PumlAssociationExporterTest {

    private final PumlAssociationExporter exporter = new PumlAssociationExporter();

    @Test
    void plainAssociationWithoutLabels() {
        IAssociation assoc = association("", IAssociation.DIRECTION_FROM_TO,
            end("", null, null, IAssociationEnd.NAVIGABLE_UNSPECIFIED, "A"),
            end("", null, null, IAssociationEnd.NAVIGABLE_UNSPECIFIED, "B"));

        assertTrue(renders(exporter, assoc, "A -- B"));
    }

    @Test
    void associationWithRoleAndMultiplicityLabels() {
        IAssociation assoc = association("", IAssociation.DIRECTION_FROM_TO,
            end("owner", "1", null, IAssociationEnd.NAVIGABLE_UNSPECIFIED, "A"),
            end("items", "0..*", null, IAssociationEnd.NAVIGABLE_UNSPECIFIED, "B"));

        assertTrue(renders(exporter, assoc, "A \"owner 1\" -- \"items 0..*\" B"));
    }

    @Test
    void namedAssociationFromToAddsTrailingArrow() {
        IAssociation assoc = association("contains", IAssociation.DIRECTION_FROM_TO,
            end("", null, null, IAssociationEnd.NAVIGABLE_UNSPECIFIED, "A"),
            end("", null, null, IAssociationEnd.NAVIGABLE_UNSPECIFIED, "B"));

        assertTrue(renders(exporter, assoc, "A -- B : contains >"));
    }

    @Test
    void namedAssociationToFromAddsLeadingArrow() {
        IAssociation assoc = association("contains", IAssociation.DIRECTION_TO_FROM,
            end("", null, null, IAssociationEnd.NAVIGABLE_UNSPECIFIED, "A"),
            end("", null, null, IAssociationEnd.NAVIGABLE_UNSPECIFIED, "B"));

        assertTrue(renders(exporter, assoc, "A -- B : < contains"));
    }

    @Test
    void namedAssociationWithoutDirectionHasPlainName() {
        IAssociation assoc = association("link", 999,
            end("", null, null, IAssociationEnd.NAVIGABLE_UNSPECIFIED, "A"),
            end("", null, null, IAssociationEnd.NAVIGABLE_UNSPECIFIED, "B"));

        assertTrue(renders(exporter, assoc, "A -- B : link"));
    }

    @Test
    void multiplicityUnspecifiedIsNormalizedAway() {
        // from 端多重度未指定，被归空，只保留 role。
        IAssociation assoc = association("", IAssociation.DIRECTION_FROM_TO,
            end("owner", IAssociationEnd.MULTIPLICITY_UNSPECIFIED, null, IAssociationEnd.NAVIGABLE_UNSPECIFIED, "A"),
            end("items", "0..*", null, IAssociationEnd.NAVIGABLE_UNSPECIFIED, "B"));

        assertTrue(renders(exporter, assoc, "A \"owner\" -- \"items 0..*\" B"));
    }

    @Test
    void compositeAggregationOnFromEndRendersSolidDiamond() {
        IAssociation assoc = association("", IAssociation.DIRECTION_FROM_TO,
            end("", null, IAssociationEnd.AGGREGATION_KIND_composite, IAssociationEnd.NAVIGABLE_UNSPECIFIED, "A"),
            end("", null, null, IAssociationEnd.NAVIGABLE_UNSPECIFIED, "B"));

        assertTrue(renders(exporter, assoc, "A *-- B"));
    }

    @Test
    void qualifiedToEndPrefixesQualifierBeforeMultiplicity() {
        // to 端带 qualifier orderNumber，端点应渲染为 [orderNumber] "0..*"。
        IAssociation assoc = association("", IAssociation.DIRECTION_FROM_TO,
            end("", "1", null, IAssociationEnd.NAVIGABLE_UNSPECIFIED, "order", (String[]) null),
            end("", "0..*", null, IAssociationEnd.NAVIGABLE_UNSPECIFIED, "lineItem",
                new String[]{"orderNumber"}));

        assertTrue(renders(exporter, assoc, "order \"1\" -- \"0..*\" [orderNumber] lineItem"));
    }

    @Test
    void qualifiedEndWithMultipleQualifiersListsThemCommaSeparated() {
        IAssociation assoc = association("", IAssociation.DIRECTION_FROM_TO,
            end("", null, null, IAssociationEnd.NAVIGABLE_UNSPECIFIED, "A", (String[]) null),
            end("", null, null, IAssociationEnd.NAVIGABLE_UNSPECIFIED, "B",
                new String[]{"orderNumber", "otherAttr"}));

        assertTrue(renders(exporter, assoc, "A -- [orderNumber\\notherAttr] B"));
    }

    @Test
    void blankQualifierNamesAreIgnored() {
        IAssociation assoc = association("", IAssociation.DIRECTION_FROM_TO,
            end("", null, null, IAssociationEnd.NAVIGABLE_UNSPECIFIED, "A", (String[]) null),
            end("", null, null, IAssociationEnd.NAVIGABLE_UNSPECIFIED, "B",
                new String[]{"", null}));

        assertTrue(renders(exporter, assoc, "A -- B"));
    }

    @Test
    void navigableEndsRenderArrows() {
        // from 端可导航 → 反向箭头"<"，to 端可导航 → 正向箭头">"。
        IAssociation assoc = association("", IAssociation.DIRECTION_FROM_TO,
            end("", null, null, IAssociationEnd.NAVIGABLE_NAVIGABLE, "A"),
            end("", null, null, IAssociationEnd.NAVIGABLE_NAVIGABLE, "B"));

        assertTrue(renders(exporter, assoc, "A <--> B"));
    }

    @Test
    void connectorEndpointsTakeAliasFromShapes() {
        // 连接线视图端点存在且非 qualifier：别名使用 shape 的模型元素 + 图元素ID。
        IAssociation assoc = association("", IAssociation.DIRECTION_FROM_TO,
            end("", null, null, IAssociationEnd.NAVIGABLE_UNSPECIFIED, "A"),
            end("", null, null, IAssociationEnd.NAVIGABLE_UNSPECIFIED, "B"));
        IShapeUIModel fromShape = shape(model("Class", "A", "m1"), "s1",
            IShapeTypeConstants.SHAPE_TYPE_CLASS);
        IShapeUIModel toShape = shape(model("Class", "B", "m2"), "s2",
            IShapeTypeConstants.SHAPE_TYPE_CLASS);

        assertTrue(rendersWithConnector(exporter, assoc, fromShape, toShape,
            "Class__A__m1__s1 -- Class__B__m2__s2"));
    }

    @Test
    void connectorQualifierEndpointsUnwrapToParent() {
        // qualifier shape 应上溯取其 parent 作为端点。
        IAssociation assoc = association("", IAssociation.DIRECTION_FROM_TO,
            end("", null, null, IAssociationEnd.NAVIGABLE_UNSPECIFIED, "A"),
            end("", null, null, IAssociationEnd.NAVIGABLE_UNSPECIFIED, "B"));
        IShapeUIModel fromParent = shape(model("Class", "A", "m1"), "s1",
            IShapeTypeConstants.SHAPE_TYPE_CLASS);
        IShapeUIModel toParent = shape(model("Class", "B", "m2"), "s2",
            IShapeTypeConstants.SHAPE_TYPE_CLASS);
        IQualifierUIModel fromQualifierUi = qualifierShape(fromParent);
        IQualifierUIModel toQualifierUi = qualifierShape(toParent);

        assertTrue(rendersWithConnector(exporter, assoc, fromQualifierUi, toQualifierUi,
            "Class__A__m1__s1 -- Class__B__m2__s2"));
    }

    @Test
    void inlineSharedAggregationRendersHollowDiamond() {
        // shared 聚合（from 端）→ o。
        IAssociation assoc = association("", IAssociation.DIRECTION_FROM_TO,
            end("", null, IAssociationEnd.AGGREGATION_KIND_shared, IAssociationEnd.NAVIGABLE_UNSPECIFIED, "A"),
            end("", null, null, IAssociationEnd.NAVIGABLE_UNSPECIFIED, "B"));

        assertTrue(renders(exporter, assoc, "A o-- B"));
    }

    @Test
    void nonNavigableEndRendersCross() {
        // 非导航端（from 端）→ x。
        IAssociation assoc = association("", IAssociation.DIRECTION_FROM_TO,
            end("", null, null, IAssociationEnd.NAVIGABLE_NON_NAVIGABLE, "A"),
            end("", null, null, IAssociationEnd.NAVIGABLE_UNSPECIFIED, "B"));

        assertTrue(renders(exporter, assoc, "A x-- B"));
    }

    @Test
    void connectorWithNullFromShapeFallsBackToModelAlias() {
        // from 端连接线存在但视图缺 shape：别名退回按模型定位（模型仅名称 → "A"）。
        IAssociation assoc = association("", IAssociation.DIRECTION_FROM_TO,
            end("", null, null, IAssociationEnd.NAVIGABLE_UNSPECIFIED, "A"),
            end("", null, null, IAssociationEnd.NAVIGABLE_UNSPECIFIED, "B"));
        IShapeUIModel toShape = shape(model("Class", "B", "m2"), "s2");
        IConnectorUIModel connector = connectorWithShapes(assoc, null, toShape);

        PumlDiagram diagram = new PumlDiagram("d");
        exporter.export(diagram, connector);
        assertTrue(diagram.toString().contains("A -- Class__B__m2__s2"));
    }

    @Test
    void connectorWithNullToShapeFallsBackToModelAlias() {
        // to 端连接线存在但视图缺 shape：别名退回按模型定位（模型仅名称 → "B"）。
        IAssociation assoc = association("", IAssociation.DIRECTION_FROM_TO,
            end("", null, null, IAssociationEnd.NAVIGABLE_UNSPECIFIED, "A"),
            end("", null, null, IAssociationEnd.NAVIGABLE_UNSPECIFIED, "B"));
        IShapeUIModel fromShape = shape(model("Class", "A", "m1"), "s1");
        IConnectorUIModel connector = connectorWithShapes(assoc, fromShape, null);

        PumlDiagram diagram = new PumlDiagram("d");
        exporter.export(diagram, connector);
        assertTrue(diagram.toString().contains("Class__A__m1__s1 -- B"));
    }

    @Test
    void connectorWithNullShapeModelFallsBackToModelAlias() {
        // from 端 shape 存在但其模型元素为 null：退回按模型定位（模型仅名称 → "A"）。
        IAssociation assoc = association("", IAssociation.DIRECTION_FROM_TO,
            end("", null, null, IAssociationEnd.NAVIGABLE_UNSPECIFIED, "A"),
            end("", null, null, IAssociationEnd.NAVIGABLE_UNSPECIFIED, "B"));
        IShapeUIModel fromShape = shape(null, "s1");
        IShapeUIModel toShape = shape(model("Class", "B", "m2"), "s2");
        IConnectorUIModel connector = connectorWithShapes(assoc, fromShape, toShape);

        PumlDiagram diagram = new PumlDiagram("d");
        exporter.export(diagram, connector);
        assertTrue(diagram.toString().contains("A -- Class__B__m2__s2"));
    }

    // ---------- helpers ----------

    private static boolean renders(PumlAssociationExporter exporter, IAssociation assoc,
        String expectedEdge) {
        PumlDiagram diagram = new PumlDiagram("d");
        exporter.export(diagram, elementOf(assoc));
        return diagram.toString().contains(expectedEdge);
    }

    private static boolean rendersWithConnector(PumlAssociationExporter exporter, IAssociation assoc,
        IShapeUIModel fromShape, IShapeUIModel toShape, String expectedEdge) {
        PumlDiagram diagram = new PumlDiagram("d");
        exporter.export(diagram, connectorElementOf(assoc, fromShape, toShape));
        return diagram.toString().contains(expectedEdge);
    }

    private static IConnectorUIModel connectorElementOf(IAssociation assoc,
        IShapeUIModel fromShape, IShapeUIModel toShape) {
        IConnectorUIModel e = mock(IConnectorUIModel.class);
        when(e.getModelElement()).thenReturn(assoc);
        when(e.getFromShape()).thenReturn(fromShape);
        when(e.getToShape()).thenReturn(toShape);
        return e;
    }

    private static IConnectorUIModel connectorWithShapes(IAssociation assoc,
        IShapeUIModel fromShape, IShapeUIModel toShape) {
        IConnectorUIModel e = mock(IConnectorUIModel.class);
        when(e.getModelElement()).thenReturn(assoc);
        when(e.getFromShape()).thenReturn(fromShape);
        when(e.getToShape()).thenReturn(toShape);
        return e;
    }

    private static IShapeUIModel shape(IModelElement model, String id, String shapeType) {
        IShapeUIModel s = mock(IShapeUIModel.class);
        when(s.getModelElement()).thenReturn(model);
        when(s.getId()).thenReturn(id);
        when(s.getShapeType()).thenReturn(shapeType);
        return s;
    }

    private static IShapeUIModel shape(IModelElement model, String id) {
        return shape(model, id, IShapeTypeConstants.SHAPE_TYPE_CLASS);
    }

    private static IQualifierUIModel qualifierShape(IShapeUIModel parent) {
        IQualifierUIModel q = mock(IQualifierUIModel.class);
        when(q.getShapeType()).thenReturn(IShapeTypeConstants.SHAPE_TYPE_QUALIFIER);
        // 外层守卫要求 shape 的模型元素非空；其 parent 提供真实端点。
        when(q.getModelElement()).thenReturn(mock(IModelElement.class));
        when(q.getParent()).thenReturn(parent);
        return q;
    }

    private static IModelElement model(String type, String name, String id) {
        IModelElement m = mock(IModelElement.class);
        when(m.getModelType()).thenReturn(type);
        when(m.getName()).thenReturn(name);
        when(m.getId()).thenReturn(id);
        return m;
    }

    private static IDiagramElement elementOf(IAssociation assoc) {
        IDiagramElement e = mock(IDiagramElement.class);
        when(e.getModelElement()).thenReturn(assoc);
        return e;
    }

    private static IAssociation association(String name, int direction,
        IAssociationEnd from, IAssociationEnd to) {
        IAssociation a = mock(IAssociation.class);
        when(a.getName()).thenReturn(name);
        when(a.getDirection()).thenReturn(direction);
        when(a.stereotypeModelIterator()).thenReturn(Collections.emptyIterator());
        when(a.getFromEnd()).thenReturn(from);
        when(a.getToEnd()).thenReturn(to);
        return a;
    }

    private static IAssociationEnd end(String role, String multiplicity,
        String aggregation, int navigable, String alias) {
        return end(role, multiplicity, aggregation, navigable, alias, null);
    }

    private static IAssociationEnd end(String role, String multiplicity,
        String aggregation, int navigable, String alias, String[] qualifierNames) {
        IAssociationEnd e = mock(IAssociationEnd.class);
        when(e.getName()).thenReturn(role);
        when(e.getMultiplicity()).thenReturn(multiplicity);
        when(e.getAggregationKind()).thenReturn(aggregation);
        when(e.getNavigable()).thenReturn(navigable);
        IModelElement m = mock(IModelElement.class);
        when(m.getName()).thenReturn(alias);
        when(m.getParent()).thenReturn(null);
        when(e.getModelElement()).thenReturn(m);
        if (qualifierNames == null) {
            when(e.getQualifier()).thenReturn(null);
        } else {
            IQualifier q = mock(IQualifier.class);
            IAttribute[] attributes = new IAttribute[qualifierNames.length];
            for (int i = 0; i < qualifierNames.length; i++) {
                IAttribute attribute = mock(IAttribute.class);
                when(attribute.getName()).thenReturn(qualifierNames[i]);
                attributes[i] = attribute;
            }
            when(q.toAttributeArray()).thenReturn(attributes);
            when(e.getQualifier()).thenReturn(q);
        }
        return e;
    }
}
