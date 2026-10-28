package plugins.vpcli.domain.outputmodel.pumelementexporter;

import com.vp.plugin.diagram.IDiagramElement;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.outputmodel.PumlDiagram;

@Singleton
public class PumlModelRelationshipContainerExporter extends PumlElementExporter {

    public static final String SHAPE_TYPE_MODEL_RELATIONSHIP_CONTAINER = "ModelRelationshipContainer_is_not_a_true_shape_type";


    @Inject
    public PumlModelRelationshipContainerExporter() {
    }

    @Override
    public String getShapeType() {
        // 该容器类型无独立 SHAPE_TYPE 常量，用模型类型字符串表征（单一来源）
        return SHAPE_TYPE_MODEL_RELATIONSHIP_CONTAINER;
    }

    // 步 A11：方法体仍为空；未实现元素不打入图，仅走空体即丢弃
    @Override
    public void export(PumlDiagram result, IDiagramElement vpDiagramElement) {
    }
}
