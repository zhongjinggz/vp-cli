package plugins.vpcli.domain.myuml.mydiagram;

import static com.vp.plugin.diagram.IShapeTypeConstants.SHAPE_TYPE_ASSOCIATION;
import static com.vp.plugin.diagram.IShapeTypeConstants.SHAPE_TYPE_CLASS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.myuml.myassociation.MyAssociation;
import plugins.vpcli.domain.myuml.myassociation.MyAssociationFactory;
import plugins.vpcli.domain.myuml.myclassifier.MyClassFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactoryFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElementPool;
import plugins.vpcli.domain.myuml.mycommon.MyElementType;
import plugins.vpcli.domain.myuml.mydiagram.mydiagramelement.DiagramElementType;
import plugins.vpcli.domain.myuml.mydiagram.mydiagramelement.MyDiagramElementFactory;
import plugins.vpcli.domain.myuml.mydiagram.mydiagramelement.ProperMyDiagramElement;
import plugins.vpcli.domain.myuml.mymodel.MyModelFactory;
import plugins.vpcli.domain.myuml.mypackage.MyPackageFactory;
import plugins.vpcli.domain.myuml.mymodelrelationshipcontainer.MyModelRelationshipContainerFactory;
import plugins.vpcli.domain.myuml.myproject.MyProject;
import plugins.vpcli.domain.myuml.myusecase.MyUseCaseFactory;

import com.vp.plugin.diagram.IConnectorUIModel;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.factory.IModelElementFactory;

class MyDiagramFactoryTest {

    private final MyProject root = new MyProject("p", "项目");

    private MyDiagramFactory buildFactory() {
        MyElementPool pool = mock(MyElementPool.class);
        when(pool.getMyProject()).thenReturn(root);

        MyElementFactoryFactory factoryFactory = new MyElementFactoryFactory(
            new MyPackageFactory(pool),
            new MyClassFactory(pool),
            new MyModelFactory(pool),
            new MyUseCaseFactory(pool),
            new MyAssociationFactory(pool),
            new MyModelRelationshipContainerFactory(pool),
            new plugins.vpcli.domain.myuml.mycommon.MyStereotypeFactory(pool)
        );

        MyDiagramElementFactory diagramElementFactory = new MyDiagramElementFactory(factoryFactory);
        return new MyDiagramFactory(diagramElementFactory);
    }

    private IShapeUIModel mockClassShape(String id, String name) {
        IModelElement vpClass = mock(IModelElement.class);
        when(vpClass.getModelType()).thenReturn(IModelElementFactory.MODEL_TYPE_CLASS);
        when(vpClass.getId()).thenReturn(id);
        when(vpClass.getName()).thenReturn(name);
        when(vpClass.getDescription()).thenReturn("");
        when(vpClass.getParent()).thenReturn(null);

        IShapeUIModel shape = mock(IShapeUIModel.class);
        when(shape.getShapeType()).thenReturn(SHAPE_TYPE_CLASS);
        when(shape.getId()).thenReturn(id + "-shape");
        when(shape.getParent()).thenReturn(null);
        when(shape.getModelElement()).thenReturn(vpClass);
        when(shape.toChildArray()).thenReturn(new IShapeUIModel[0]);
        return shape;
    }

    private IConnectorUIModel mockAssociationConnector(
        IModelElement vpFrom, IModelElement vpTo, String connectorId
    ) {
        IAssociation association = mock(IAssociation.class);
        when(association.getModelType()).thenReturn(IModelElementFactory.MODEL_TYPE_ASSOCIATION);
        when(association.getId()).thenReturn(connectorId);
        when(association.getName()).thenReturn("");
        when(association.getDescription()).thenReturn("");
        when(association.getFrom()).thenReturn(vpFrom);
        when(association.getTo()).thenReturn(vpTo);

        IConnectorUIModel connector = mock(IConnectorUIModel.class);
        when(connector.getShapeType()).thenReturn(SHAPE_TYPE_ASSOCIATION);
        when(connector.getId()).thenReturn(connectorId + "-connector");
        when(connector.getModelElement()).thenReturn(association);
        return connector;
    }

    @Test
    void shouldCollectAssociationConnectorIntoSubEdges() {
        IShapeUIModel classAShape = mockClassShape("c1", "订单");
        IShapeUIModel classBShape = mockClassShape("c2", "客户");

        IConnectorUIModel associationConnector =
            mockAssociationConnector(classAShape.getModelElement(), classBShape.getModelElement(), "a1");

        IDiagramUIModel vpDiagram = mock(IDiagramUIModel.class);
        when(vpDiagram.getId()).thenReturn("d1");
        when(vpDiagram.getName()).thenReturn("类图");
        when(vpDiagram.getDocumentation()).thenReturn("");
        when(vpDiagram.getType()).thenReturn("ClassDiagram");
        when(vpDiagram.toShapeUIModelArray()).thenReturn(new IShapeUIModel[]{classAShape, classBShape});
        when(vpDiagram.toConnectorUIModelArray()).thenReturn(new IConnectorUIModel[]{associationConnector});

        MyDiagram result = buildFactory().of(vpDiagram, root);

        assertEquals(2, result.getSubShapes().size());
        assertEquals(1, result.getSubEdges().size());

        ProperMyDiagramElement edge = result.getSubEdges().get(0);
        assertEquals(DiagramElementType.ASSOCIATION, edge.getDiagramElementType());
        assertTrue(edge.isEdge());
        assertFalse(edge.isShape());

        MyElement modelElement = edge.getModelElement();
        assertEquals(MyElementType.ASSOCIATION, modelElement.getElementType());
        assertTrue(modelElement instanceof MyAssociation);

        MyAssociation association = (MyAssociation) modelElement;
        assertEquals("订单", association.getFromElement().getName());
        assertEquals(MyElementType.CLASS, association.getFromElement().getElementType());
        assertEquals("客户", association.getToElement().getName());
        assertEquals(MyElementType.CLASS, association.getToElement().getElementType());
    }

    @Test
    void shouldIgnoreNonAssociationConnectors() {
        IShapeUIModel classAShape = mockClassShape("c1", "订单");

        // 一个非关联连接线（例如依赖），shapeType 未映射到 ASSOCIATION
        IConnectorUIModel otherConnector = mock(IConnectorUIModel.class);
        when(otherConnector.getShapeType()).thenReturn("Dependency");

        IDiagramUIModel vpDiagram = mock(IDiagramUIModel.class);
        when(vpDiagram.getId()).thenReturn("d1");
        when(vpDiagram.getName()).thenReturn("类图");
        when(vpDiagram.getDocumentation()).thenReturn("");
        when(vpDiagram.getType()).thenReturn("ClassDiagram");
        when(vpDiagram.toShapeUIModelArray()).thenReturn(new IShapeUIModel[]{classAShape});
        when(vpDiagram.toConnectorUIModelArray()).thenReturn(new IConnectorUIModel[]{otherConnector});

        MyDiagram result = buildFactory().of(vpDiagram, root);

        assertEquals(1, result.getSubShapes().size());
        assertEquals(0, result.getSubEdges().size());
    }

    @Test
    void shouldHandleNullConnectorArray() {
        IShapeUIModel classAShape = mockClassShape("c1", "订单");

        IDiagramUIModel vpDiagram = mock(IDiagramUIModel.class);
        when(vpDiagram.getId()).thenReturn("d1");
        when(vpDiagram.getName()).thenReturn("类图");
        when(vpDiagram.getDocumentation()).thenReturn("");
        when(vpDiagram.getType()).thenReturn("ClassDiagram");
        when(vpDiagram.toShapeUIModelArray()).thenReturn(new IShapeUIModel[]{classAShape});
        when(vpDiagram.toConnectorUIModelArray()).thenReturn(null);

        MyDiagram result = buildFactory().of(vpDiagram, root);

        assertEquals(1, result.getSubShapes().size());
        assertEquals(0, result.getSubEdges().size());
    }
}
