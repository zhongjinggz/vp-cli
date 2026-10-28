package plugins.vpcli.domain.outputmodel.pumelementexporter;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.model.IModelElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plugins.vpcli.domain.outputmodel.PumlDiagram;

public abstract class PumlBlockExporter extends PumlElementExporter {

    private static final Logger logger = LoggerFactory.getLogger(PumlBlockExporter.class);

    public PumlBlockExporter() {
    }

    @Override
    public void export(PumlDiagram result, IDiagramElement vpDiagramElement) {
        IModelElement vpElement = vpDiagramElement.getModelElement();

        result.beginBlock(
            getBlockType(),
            vpElement.getName(),
            getPumlAlias(vpElement),
            getBlockSuffix()
        );

        writeBlockBody(result, vpElement);

        if (vpDiagramElement instanceof IShapeUIModel) {
            IShapeUIModel shape = (IShapeUIModel) vpDiagramElement;
            for (IDiagramElement child : shape.toChildArray()) {
                if (child.getModelElement() == null) {
                    continue;
                }
                var exporter = pumlElementExporterFactory.build(child.getShapeType());
                exporter.export(result, child);
            }
        }
        result.endBlock();
    }

    abstract String getBlockType();

    // 供需要在块声明末尾追加内容（如 <<model>> 原型）的子类覆写
    String getBlockSuffix() {
        return "";
    }

    // 步 A7：VP 原生读法的成员区钩子；默认空实现，子类按需覆写
    void writeBlockBody(PumlDiagram result, IModelElement vpElement) {
    }
}
