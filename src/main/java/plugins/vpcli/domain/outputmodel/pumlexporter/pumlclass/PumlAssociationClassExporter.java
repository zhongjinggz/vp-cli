package plugins.vpcli.domain.outputmodel.pumlexporter.pumlclass;

import com.vp.plugin.diagram.IConnectorUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationClass;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlDiagram;
import plugins.vpcli.domain.outputmodel.pumlexporter.PumlElementExporter;

// 关联类：从关联线到关联类别名的虚连线。VP 中该连接线的模型元素是 IAssociationClass，
// 其两端分别是「关联 IAssociation（含两个被关联类）」与「关联类 IClass」。
@Singleton
public class PumlAssociationClassExporter extends PumlElementExporter {

    @Inject
    public PumlAssociationClassExporter() {
    }

    @Override
    public String getShapeType() {
        return IShapeTypeConstants.SHAPE_TYPE_ASSOCIATION_CLASS;
    }

    @Override
    public void export(PumlDiagram result, IDiagramElement vpDiagramElement) {
        IConnectorUIModel connector = (IConnectorUIModel) vpDiagramElement;
        IModelElement model = connector.getModelElement();
        if (!(model instanceof IAssociationClass)) {
            return;
        }

        IAssociationClass associationClass = (IAssociationClass) model;
        IModelElement from = associationClass.getFrom();
        IModelElement to = associationClass.getTo();

        IAssociation association;
        IClass clazz;
        if (from instanceof IAssociation) {
            association = (IAssociation) from;
            clazz = (IClass) to;
        } else {
            association = (IAssociation) to;
            clazz = (IClass) from;
        }

        IModelElement participant1 = association.getFromEnd().getModelElement();
        IModelElement participant2 = association.getToEnd().getModelElement();

        result.writeText(
            "(" + getPumlAlias(participant1)
                + ", " + getPumlAlias(participant2)
                + ") .. " + getPumlAlias(clazz)
        );
    }

}
