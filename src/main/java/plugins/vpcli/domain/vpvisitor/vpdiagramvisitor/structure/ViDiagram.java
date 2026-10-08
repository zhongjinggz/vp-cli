package plugins.vpcli.domain.vpvisitor.vpdiagramvisitor.structure;

import com.vp.plugin.diagram.IConnectorUIModel;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.IShapeUIModel;
import org.checkerframework.checker.nullness.qual.NonNull;
import plugins.vpcli.domain.vpvisitor.vpdiagramvisitor.element.ViDiagramElement;
import plugins.vpcli.domain.vpvisitor.vpdiagramvisitor.element.ViDiagramElementFactory;
import plugins.vpcli.domain.vpvisitor.vpdiagramvisitor.visitor.ViDiagramElementVisitor;
import plugins.vpcli.domain.vpvisitor.vpdiagramvisitor.visitor.ViDiagramElementVisitorFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ViDiagram {
    private final IDiagramUIModel vpDiagram;
    private final ViDiagramElementFactory createElement;
    private ViDiagramElementVisitorFactory visitorFactory;

    public ViDiagram(IDiagramUIModel vpDiagram
        , ViDiagramElementFactory elementFactory) {

        this.vpDiagram = vpDiagram;
        this.createElement = elementFactory;
    }

    public void accept(ViDiagramElementVisitorFactory visitorFactory) throws IOException {
        this.visitorFactory = visitorFactory;
        List<ViDiagramElement> elements = createTopLevelElements();
        elementsAccept(elements, null);
    }

    private @NonNull List<ViDiagramElement> createTopLevelElements() {
        List<ViDiagramElement> result = new ArrayList<>();

        var vpShapes = vpDiagram.toShapeUIModelArray();
        if (vpShapes != null) {
            for (IShapeUIModel aShape : vpShapes) {
                result.add(createElement.from(aShape));
            }
        }

        var vpConnectors = vpDiagram.toConnectorUIModelArray();
        if (vpConnectors != null) {
            for (IConnectorUIModel aConnector : vpConnectors) {
                result.add(createElement.from(aConnector));
            }
        }

        return result;
    }

    private void elementsAccept(List<ViDiagramElement> elements
        , ViDiagramElementVisitor preLevelVisitor) throws IOException {

        int size = elements.size();
        int i = 0;

        for (var anElement : elements) {
            var visitor = visitorFactory.create(preLevelVisitor);
            visitor.setLast(i + 1 == size);
            visitor.visit(anElement);

            elementsAccept(
                anElement.getChildren()
                , visitor
            );

            i++;
        }
    }
}
