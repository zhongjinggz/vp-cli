package plugins.vpcli.domain.outputmodel.pumlexporter.pumlclass;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.model.IModelElement;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlDiagram;
import plugins.vpcli.domain.outputmodel.pumlexporter.PumlElementExporter;
import plugins.vpcli.util.Strings;

@Singleton
public class PumlInterfaceBallExporter extends PumlElementExporter {
    public static final String SHAPE_TYPE = "pseudo_interface_ball";

    @Inject
    public PumlInterfaceBallExporter() {
    }

    @Override
    public String getShapeType() {
        return this.SHAPE_TYPE;
    }

    /**
     * 步 A9：直读 VP 原生模型。use case 对应的 IModelElement 提供 getName() 与 VP 全限定名别名。
     */
    @Override
    public void export(PumlDiagram result, IDiagramElement vpDiagramElement) {
        IModelElement vpElement = vpDiagramElement.getModelElement();
        result.writeText(
            "circle " +
                Strings.doubleQuote(vpElement.getName()) +
                " as " +
                getPumlAlias(vpElement)
        );
    }
}
