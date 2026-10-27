package plugins.vpcli.domain.outputmodel.pumelementexporter;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.model.IModelElement;
import org.slf4j.Logger;
import plugins.vpcli.domain.outputmodel.PumlDiagram;

import static org.slf4j.LoggerFactory.getLogger;

public abstract class PumlBlockExporter extends PumlElementExporter {

    private final Logger logger;

    public PumlBlockExporter() {
        this.logger = getLogger(PumlBlockExporter.class);
    }

    /**
     * 步 A7：块型基类直读 VP 原生模型。块名/别名从 vpDiagramElement 的 IModelElement 读取，
     * 子形状沿 IShapeUIModel.toChildArray() 遍历（跳过无模型元素的形状，如属性文本等
     * terminal shape，与迁移前 myuml 的 subShapes 一致，保证输出等价）。
     * 块属性（成员区）经 VP 版 {@link #writeBlockBody(PumlDiagram, IModelElement)} 钩子渲染。
     */
    @Override
    public void to(PumlDiagram result, IDiagramElement vpDiagramElement) {
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
                var exporter = pumlElementExporterFactory.of(child.getShapeType());
                exporter.to(result, child);
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
