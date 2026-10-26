package plugins.vpcli.domain.outputmodel.pumelementexporter;

import com.vp.plugin.diagram.IShapeTypeConstants;
import plugins.vpcli.domain.myuml.mydiagram.mydiagramelement.ProperMyDiagramElement;

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

    public void to(PumlDiagram result, ProperMyDiagramElement myDiagramElement) {
        // it seems this will never been called. remove it later. Use Null Object pattern?

    }
}
