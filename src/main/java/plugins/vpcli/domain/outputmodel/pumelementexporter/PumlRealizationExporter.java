package plugins.vpcli.domain.outputmodel.pumelementexporter;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.model.IRealization;
import plugins.vpcli.domain.outputmodel.NameDirection;
import plugins.vpcli.domain.outputmodel.PumlDiagram;
import plugins.vpcli.domain.outputmodel.PumlDiagram.LineStyle;
import plugins.vpcli.domain.outputmodel.PumlEdgeEnd;
import plugins.vpcli.domain.outputmodel.PumlEdgeEnd.ShapeKind;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class PumlRealizationExporter extends PumlElementExporter {
    @Inject
    public PumlRealizationExporter() {
    }

    @Override
    public String getShapeType() {
        return IShapeTypeConstants.SHAPE_TYPE_REALIZATION;
    }

    /**
     * 步 #18：直读 VP 原生实现关系（Element Type = Realization）。实现类在 getFrom、接口在 getTo，
     * 渲染为虚线 + from 端空心三角：`实现类 <|.. 接口`（沿用泛化的三角在 from 端语义）。
     */
    @Override
    public void export(PumlDiagram result, IDiagramElement vpDiagramElement) {
        IRealization realization = (IRealization) vpDiagramElement.getModelElement();

        String fromAlias = getPumlAlias(realization.getFrom());
        String toAlias = getPumlAlias(realization.getTo());

        result.writeEdge(
            PumlEdgeEnd.of(fromAlias, "", "", ShapeKind.REVERSE_TRIANGLE),
            PumlEdgeEnd.of(toAlias, "", "", ShapeKind.NONE),
            LineStyle.DASHED,
            buildEdgeLabel(realization.getName(), realization),
            NameDirection.NONE);
    }
}
