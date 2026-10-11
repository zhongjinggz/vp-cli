package plugins.vpcli.domain.vpvisitor.vpdiagramvisitor.structure;

import com.vp.plugin.diagram.IConnectorUIModel;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.IShapeUIModel;
import org.checkerframework.checker.nullness.qual.NonNull;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagram;
import plugins.vpcli.domain.myuml.mydiagram.BaseMyDiagramElement;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagramElement;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagramFactory;
import plugins.vpcli.domain.vpvisitor.vpdiagramvisitor.element.MyDiagramElementFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VPToMyDiagramConverter {
    private final MyDiagramElementFactory createElement;
    private MyDiagramFactory myDiagramFactory;

    public VPToMyDiagramConverter(
        MyDiagramFactory myDiagramFactory
        , MyDiagramElementFactory elementFactory) {

        this.createElement = elementFactory;
        this.myDiagramFactory = myDiagramFactory;
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
        MyDiagram myDiagram = myDiagramFactory.create(parentElement, vpDiagram);
//        List<MyDiagramElement> myElements = createTopLevelElements(vpDiagram);
//        createSubElements(myElements, myDiagram);
        return null;
    }
}
