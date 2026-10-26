package plugins.vpcli.domain.outputmodel.pumelementexporter;

import com.vp.plugin.diagram.IShapeTypeConstants;
import plugins.vpcli.domain.myuml.mydependency.MyDependency;
import plugins.vpcli.domain.myuml.mydiagram.mydiagramelement.ProperMyDiagramElement;
import plugins.vpcli.domain.myuml.myassociation.MyAssociation;
import plugins.vpcli.domain.outputmodel.PumlDiagram;
import plugins.vpcli.domain.outputmodel.PumlDiagram.LineStyle;
import plugins.vpcli.domain.outputmodel.PumlEdgeEnd;
import plugins.vpcli.domain.outputmodel.PumlEdgeEnd.ShapeKind;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class PumlDependencyExporter extends PumlElementExporter {
    @Inject
    public PumlDependencyExporter() {
    }

    @Override
    public String getShapeType() {
        return IShapeTypeConstants.SHAPE_TYPE_DEPENDENCY;
    }

    @Override
    public void to(PumlDiagram result, ProperMyDiagramElement myDiagramElement) {
        var dependency = (MyDependency) myDiagramElement.getModelElement();

        String fromAlias = getPumlAlias(dependency.getFrom());
        String toAlias = getPumlAlias(dependency.getTo());

        // 依赖是“边”：虚线 + to 端箭头，from 恒在左、to 恒在右，阅读方向三角恒不画
        result.writeEdge(
            PumlEdgeEnd.of(fromAlias, "", "", ShapeKind.NONE),
            PumlEdgeEnd.of(toAlias, "", "", ShapeKind.ARROW),
            LineStyle.DASHED,
            dependency.getName(),
            MyAssociation.NameDirection.NONE);
    }
}
