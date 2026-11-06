package plugins.vpcli.domain.outputmodel.pumlexporter.pumlclass;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlDiagram;
import plugins.vpcli.domain.outputmodel.pumlexporter.PumlElementExporter;

// 多元（n-ary）关联：中心菱形（SHAPE_TYPE_NARY，模型为 INARY）渲染为 diamond <别名>，
// 各参与类以带多重性的实线 -- 连到菱形。
@Singleton
public class PumlNAryExporter extends PumlElementExporter {

    @Inject
    public PumlNAryExporter() {
    }

    @Override
    public String getShapeType() {
        return IShapeTypeConstants.SHAPE_TYPE_NARY;
    }

    @Override
    public void export(PumlDiagram result, IDiagramElement vpDiagramElement) {

        result.writeText(
            "diamond " + pumlAlias(vpDiagramElement.getModelElement())
        );
    }

}
