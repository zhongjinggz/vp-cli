package vpcli.plugin.domain.outputmodel.pumlexporter;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.model.IModelElement;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vpcli.plugin.domain.outputmodel.pumldiagram.PumlDiagram;

import static vpcli.plugin.util.Strings.normalizeNull;

@Singleton
public class PumlUnimplementedExporter extends PumlElementExporter {
    private static final Logger log = LoggerFactory.getLogger(PumlUnimplementedExporter.class);

    public static final String SHAPE_TYPE_UNIMPLEMENTED = "UnimplementedShapeType_is_not_a_true_shape_type";

    @Inject
    public PumlUnimplementedExporter() {
    }

    @Override
    public String getShapeType() {
        // 未实现元素在 IShapeTypeConstants 中没有对应的常量，因此，用自定义常量作为占位符
        return SHAPE_TYPE_UNIMPLEMENTED;
    }

    /**
     * 步 A10：直读 VP 原生模型。未实现元素不打入图，仅读 VP 元素名称/类型打警告日志。
     */
    @Override
    public void export(PumlDiagram result, IDiagramElement vpDiagramElement) {
        IModelElement vpElement = vpDiagramElement.getModelElement();


        log.warn(
            "Unimplemented diagram element will not be exported, {} shape-type={}  model-type ={} model-name={}",
            vpElement == null ? "model-element-is-null" : "",
            normalizeNull(vpDiagramElement.getShapeType()),
            vpElement == null ? "null" : normalizeNull(vpElement.getModelType()),
            vpElement == null ? "null" : normalizeNull(vpElement.getName())
        );
    }
}
