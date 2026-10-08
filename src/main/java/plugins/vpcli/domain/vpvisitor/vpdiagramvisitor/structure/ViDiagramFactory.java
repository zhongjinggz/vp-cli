package plugins.vpcli.domain.vpvisitor.vpdiagramvisitor.structure;

import com.vp.plugin.diagram.IDiagramUIModel;
import plugins.vpcli.domain.vpvisitor.vpdiagramvisitor.element.ViDiagramElementFactory;

public class ViDiagramFactory {
    public ViDiagram create(IDiagramUIModel diagram) {
        return new ViDiagram(diagram, new ViDiagramElementFactory());
    }
}
