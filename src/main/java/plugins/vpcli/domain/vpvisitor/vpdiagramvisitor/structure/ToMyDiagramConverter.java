package plugins.vpcli.domain.vpvisitor.vpdiagramvisitor.structure;

import com.vp.plugin.diagram.IDiagramUIModel;
import org.checkerframework.checker.nullness.qual.NonNull;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagram;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagramElement;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagramCreator;
import plugins.vpcli.domain.vpvisitor.vpdiagramvisitor.element.MyDiagramElementFactory;

import java.util.ArrayList;
import java.util.List;

public class ToMyDiagramConverter {
    private final MyDiagramElementFactory createElement;
    private MyDiagramCreator myDiagramCreator;

    public ToMyDiagramConverter(
        MyDiagramCreator myDiagramCreator
        , MyDiagramElementFactory elementFactory) {

        this.createElement = elementFactory;
        this.myDiagramCreator = myDiagramCreator;
    }

    private @NonNull List<MyDiagramElement> createTopLevelElements(IDiagramUIModel vpDiagram) {
        List<MyDiagramElement> result = new ArrayList<>();

//        var vpShapes = vpDiagram.toShapeUIModelArray();
//        if (vpShapes != null) {
//            for (IShapeUIModel aShape : vpShapes) {
//                result.add(createElement.from(aShape, modelElement, parentDiagramElement));
//            }
//        }
//
//        var vpConnectors = vpDiagram.toConnectorUIModelArray();
//        if (vpConnectors != null) {
//            for (IConnectorUIModel aConnector : vpConnectors) {
//                result.add(createElement.from(aConnector, modelElement, parentDiagramElement));
//            }
//        }

        return result;
    }

//    private void createSubElements(List<ViDiagramElement> elements
//        , ViDiagramElementVisitor preLevelVisitor) throws IOException {
//
//        int size = elements.size();
//        int i = 0;
//
//        for (var anElement : elements) {
////            var visitor = visitorFactory.create(preLevelVisitor);
////            visitor.setLast(i + 1 == size);
////            visitor.visit(anElement);
//
////            createSubElements(
////                anElement.getChildren()
////                , visitor
////            );
//
//            i++;
//        }
//    }

    public MyDiagram from(MyElement parentElement, IDiagramUIModel vpDiagram) {
        MyDiagram myDiagram = myDiagramCreator.create(parentElement, vpDiagram);
//        List<MyDiagramElement> myElements = createTopLevelElements(vpDiagram);
//        createSubElements(myElements, myDiagram);
        return null;
    }
}
