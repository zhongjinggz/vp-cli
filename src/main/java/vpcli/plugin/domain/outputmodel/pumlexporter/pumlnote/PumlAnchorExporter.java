package vpcli.plugin.domain.outputmodel.pumlexporter.pumlnote;

import com.vp.plugin.diagram.IConnectorUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import vpcli.plugin.domain.outputmodel.pumldiagram.PumlDiagram;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import vpcli.plugin.domain.outputmodel.pumlexporter.PumlElementExporter;

@Singleton
public class PumlAnchorExporter extends PumlElementExporter {
    @Inject
    public PumlAnchorExporter() {
    }

    @Override
    public String getShapeType() {
        return IShapeTypeConstants.SHAPE_TYPE_ANCHOR;
    }

    /**
     * 注释–元素之间的连线的类型是 IAnchor
     */
    @Override
    public void export(PumlDiagram result, IDiagramElement vpDiagramElement) {

        String fromAlias;
        String toAlias;
        IConnectorUIModel connector = (IConnectorUIModel) vpDiagramElement;
        fromAlias = pumlAlias(connector.getFromShape());
        toAlias = pumlAlias(connector.getToShape());

        result.writeText(formatAnchor(fromAlias, toAlias));
    }

    private String formatAnchor(String fromAlias, String toAlias) {
        return fromAlias + " .. " + toAlias;
    }
}
