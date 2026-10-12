package plugins.vpcli.domain.vpvisitor.vpdiagramvisitor.element;

import com.vp.plugin.diagram.IConnectorUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeUIModel;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mydiagram.*;
import plugins.vpcli.domain.myuml.mydiagram.DiagramElementType;

public class MyDiagramElementFactory {
    public MyDiagramElement from(IDiagramElement vpDiagramElement,
                                 MyElement modelElement,
                                 MyDiagramElement parentDiagramElement) {
//        if (vpDiagramElement instanceof IShapeUIModel) {
//            return new MyShape(
//                vpDiagramElement.getId(),
//                DiagramElementType.of(vpDiagramElement.getShapeType()),
//                modelElement,
//                parentDiagramElement
//            );
//        } else if (vpDiagramElement instanceof IConnectorUIModel) {
//            return new MyEdge(
//                vpDiagramElement.getId(),
//                DiagramElementType.of(vpDiagramElement.getShapeType()),
//                modelElement,
//                parentDiagramElement
//            );
//        }  else {
//            //TODO unified exception handling
//            return  null;
//        }
            return new ProperMyDiagramElement(
                vpDiagramElement.getId(),
                DiagramElementType.of(vpDiagramElement.getShapeType()),
                modelElement,
                parentDiagramElement
            );

    }

}
