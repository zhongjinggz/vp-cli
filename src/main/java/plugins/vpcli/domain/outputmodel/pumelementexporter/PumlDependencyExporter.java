package plugins.vpcli.domain.outputmodel.pumelementexporter;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.model.IDependency;
import plugins.vpcli.domain.outputmodel.NameDirection;
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

    /**
     * 步 A6：直接读 VP 原生依赖关系。连接线的 getModelElement() 即 IDependency；
     * 经继承自 IRelationship 的 getFrom()/getTo() 取两端分类器作别名。
     */
    @Override
    public void to(PumlDiagram result, IDiagramElement vpDiagramElement) {
        IDependency dependency = (IDependency) vpDiagramElement.getModelElement();

        String fromAlias = getPumlAlias(dependency.getFrom());
        String toAlias = getPumlAlias(dependency.getTo());

        result.writeEdge(
            PumlEdgeEnd.of(fromAlias, "", "", ShapeKind.NONE),
            PumlEdgeEnd.of(toAlias, "", "", ShapeKind.ARROW),
            LineStyle.DASHED,
            dependency.getName(),
            NameDirection.NONE);
    }
}
