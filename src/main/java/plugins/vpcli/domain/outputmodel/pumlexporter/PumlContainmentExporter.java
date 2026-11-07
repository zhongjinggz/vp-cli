package plugins.vpcli.domain.outputmodel.pumlexporter;

import com.vp.plugin.diagram.IConnectorUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlDiagram;

// inner class is only a relationship between outer class and inner class,
// so, there is no model element in it
@Singleton
public class PumlContainmentExporter extends PumlElementExporter {

    @Inject
    public PumlContainmentExporter() {

    }

    @Override
    public String getShapeType() {
        return IShapeTypeConstants.SHAPE_TYPE_CONTAINMENT;
    }

    @Override
    public void export(PumlDiagram result, IDiagramElement vpDiagramElement) {
        IConnectorUIModel containment = (IConnectorUIModel) vpDiagramElement;

        String fromAlias = pumlAlias(containment.getFromShape());
        String toAlias = pumlAlias(containment.getToShape());

        result.writeText(fromAlias + " +-- " + toAlias);
    }

}
