package plugins.vpcli.domain.outputmodel.pumelementexporter;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.model.IAnchor;
import plugins.vpcli.domain.outputmodel.NameDirection;
import plugins.vpcli.domain.outputmodel.PumlDiagram;
import plugins.vpcli.domain.outputmodel.PumlDiagram.LineStyle;
import plugins.vpcli.domain.outputmodel.PumlEdgeEnd;
import plugins.vpcli.domain.outputmodel.PumlEdgeEnd.ShapeKind;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class PumlAnchorExporter extends PumlElementExporter {
    @Inject
    public PumlAnchorExporter() {
    }

    @Override
    public String getShapeType() {
        return IShapeTypeConstants.SHAPE_TYPE_ANCHOR;
    }

    /**
     * 步 #19：渲染注释–元素之间的连线。anchor 恒无名，故 name 传空串（无冒号标签），
     * 虚线 `..` 两端为 NONE（anchor 无端导航符号）：`X .. N`。
     */
    @Override
    public void export(PumlDiagram result, IDiagramElement vpDiagramElement) {
        IAnchor anchor = (IAnchor) vpDiagramElement.getModelElement();

        String fromAlias = getPumlAlias(anchor.getFrom());
        String toAlias = getPumlAlias(anchor.getTo());

        result.writeEdge(
            PumlEdgeEnd.of(fromAlias, "", "", ShapeKind.NONE),
            PumlEdgeEnd.of(toAlias, "", "", ShapeKind.NONE),
            LineStyle.DASHED,
            "",
            NameDirection.NONE);
    }
}
