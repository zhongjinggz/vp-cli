package plugins.vpcli.domain.outputmodel.pumlexporter.pumlnote;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.model.IAnchor;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlDiagram;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.outputmodel.pumlexporter.PumlElementExporter;

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
        IAnchor anchor = (IAnchor) vpDiagramElement.getModelElement();

        String fromAlias = getPumlAlias(anchor.getFrom());
        String toAlias = getPumlAlias(anchor.getTo());

        result.writeText(formatAnchor(fromAlias, toAlias));
    }

    private String formatAnchor(String fromAlias, String toAlias) {
        return fromAlias + " .. " + toAlias;
    }
}
