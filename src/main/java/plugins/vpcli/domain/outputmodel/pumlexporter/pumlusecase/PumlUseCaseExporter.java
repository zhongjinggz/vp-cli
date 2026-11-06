package plugins.vpcli.domain.outputmodel.pumlexporter.pumlusecase;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.model.IModelElement;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlDiagram;
import plugins.vpcli.domain.outputmodel.pumlexporter.PumlElementExporter;

@Singleton
public class PumlUseCaseExporter extends PumlElementExporter {
    @Inject
    public PumlUseCaseExporter() {
    }

    @Override
    public String getShapeType() {
        return IShapeTypeConstants.SHAPE_TYPE_USE_CASE;
    }

    /**
     * 步 A9：直读 VP 原生模型。use case 对应的 IModelElement 提供 getName() 与 VP 全限定名别名。
     */
    @Override
    public void export(PumlDiagram result, IDiagramElement vpDiagramElement) {
        IModelElement vpElement = vpDiagramElement.getModelElement();
        result.writeText("usecase \"" + vpElement.getName() + "\" as " + pumlAlias(vpElement));
    }
}
