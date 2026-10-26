package plugins.vpcli.domain.outputmodel.pumelementexporter;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.model.IGeneralization;
import plugins.vpcli.domain.myuml.mydiagram.mydiagramelement.ProperMyDiagramElement;
import plugins.vpcli.domain.myuml.mygeneralization.MyGeneralization;
import plugins.vpcli.domain.outputmodel.NameDirection;
import plugins.vpcli.domain.outputmodel.PumlDiagram;
import plugins.vpcli.domain.outputmodel.PumlDiagram.LineStyle;
import plugins.vpcli.domain.outputmodel.PumlEdgeEnd;
import plugins.vpcli.domain.outputmodel.PumlEdgeEnd.ShapeKind;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class PumlGeneralizationExporter extends PumlElementExporter {
    @Inject
    public PumlGeneralizationExporter() {
    }

    @Override
    public String getShapeType() {
        return IShapeTypeConstants.SHAPE_TYPE_GENERALIZATION;
    }

    @Override
    public void to(PumlDiagram result, ProperMyDiagramElement myDiagramElement) {
        var generalization = (MyGeneralization) myDiagramElement.getModelElement();

        // VP 泛化方向恒定：getFrom()=general(父)、getTo()=specific(子)。from 恒在左。
        String generalAlias = getPumlAlias(generalization.getGeneral());
        String specificAlias = getPumlAlias(generalization.getSpecific());

        // 实线 + 空心三角，三角指向父类（general，位于 from/左侧）→ 输出 `general <|-- specific`
        result.writeEdge(
            PumlEdgeEnd.of(generalAlias, "", "", ShapeKind.REVERSE_TRIANGLE),
            PumlEdgeEnd.of(specificAlias, "", "", ShapeKind.NONE),
            LineStyle.SOLID,
            generalization.getName(),
            NameDirection.NONE);
    }

    /**
     * 步 A6：直接读 VP 原生泛化关系。连接线的 getModelElement() 即 IGeneralization；
     * getFrom()=general(父)、getTo()=specific(子)，from 恒在左，输出 `general <|-- specific`。
     */
    @Override
    public void to(PumlDiagram result, ProperMyDiagramElement myDiagramElement, IDiagramElement vpDiagramElement) {
        IGeneralization generalization = (IGeneralization) vpDiagramElement.getModelElement();

        String generalAlias = getPumlAlias(generalization.getFrom());
        String specificAlias = getPumlAlias(generalization.getTo());

        result.writeEdge(
            PumlEdgeEnd.of(generalAlias, "", "", ShapeKind.REVERSE_TRIANGLE),
            PumlEdgeEnd.of(specificAlias, "", "", ShapeKind.NONE),
            LineStyle.SOLID,
            generalization.getName(),
            NameDirection.NONE);
    }
}
