package vpcli.plugin.domain.outputmodel.pumlexporter.pumlclass;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.diagram.connector.IAssociationClassUIModel;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vpcli.plugin.domain.outputmodel.pumldiagram.PumlDiagram;
import vpcli.plugin.domain.outputmodel.pumlexporter.PumlElementExporter;

// 关联类：从关联线到关联类别名的虚连线。VP 中该连接线的模型元素是 IAssociationClass，
// 其两端分别是「关联 IAssociation（含两个被关联类）」与「关联类 IClass」。
@Singleton
public class PumlAssociationClassExporter extends PumlElementExporter {
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(PumlAssociationClassExporter.class);

    @Inject
    public PumlAssociationClassExporter() {
    }

    @Override
    public String getShapeType() {
        return IShapeTypeConstants.SHAPE_TYPE_ASSOCIATION_CLASS;
    }

    @Override
    public void export(PumlDiagram result, IDiagramElement vpDiagramElement) {
        var associationClassConnector = (IAssociationClassUIModel) vpDiagramElement;
        var associationConnector = associationClassConnector.getFromConnector();
        var associationClassShape = associationClassConnector.getToShape();
        var associationFromShape = associationConnector.getFromShape();
        var associationToShape = associationConnector.getToShape();

        result.writeText("(" + pumlAlias(associationFromShape) + ", " + pumlAlias(associationToShape) + ")"
            + " .. "
            + pumlAlias(associationClassShape)
        );
    }

}
