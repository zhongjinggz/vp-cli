package plugins.vpcli.domain.outputmodel.pumelementexporter;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.model.IRelationship;
import plugins.vpcli.domain.outputmodel.NameDirection;
import plugins.vpcli.domain.outputmodel.PumlDiagram;
import plugins.vpcli.domain.outputmodel.PumlDiagram.LineStyle;
import plugins.vpcli.domain.outputmodel.PumlEdgeEnd;
import plugins.vpcli.domain.outputmodel.PumlEdgeEnd.ShapeKind;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * #21：特殊的依赖 Special Dependency 共用的导出基类。
 * 连接线的 getModelElement() 可以是 IDependency 或 9 类特殊依赖中各自独立的模型接口
 * （如 IUsage、IBindingDependency、IPermission、IAbstraction）。它们均不继承 IDependency，
 * 但都经继承链直达 IRelationship 的 getFrom()/getTo()/getName()，故统一按 IRelationship 读取
 * 即可覆盖全部类型；渲染逻辑（虚线 + 目标端箭头 `..>`）与普通依赖完全一致。
 */
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
    public void export(PumlDiagram result, IDiagramElement vpDiagramElement) {
        IRelationship dependency = (IRelationship) vpDiagramElement.getModelElement();

        String fromAlias = getPumlAlias(dependency.getFrom());
        String toAlias = getPumlAlias(dependency.getTo());

        result.writeEdge(
            PumlEdgeEnd.of(fromAlias, "", "", ShapeKind.NONE),
            PumlEdgeEnd.of(toAlias, "", "", ShapeKind.ARROW),
            LineStyle.DASHED,
            buildEdgeLabel(dependency.getName(), dependency),
            NameDirection.NONE);
    }
}
