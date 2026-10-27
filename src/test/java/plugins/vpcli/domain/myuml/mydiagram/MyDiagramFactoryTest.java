package plugins.vpcli.domain.myuml.mydiagram;

import static com.vp.plugin.diagram.IShapeTypeConstants.SHAPE_TYPE_ASSOCIATION;
import static com.vp.plugin.diagram.IShapeTypeConstants.SHAPE_TYPE_CLASS;
import static com.vp.plugin.diagram.IShapeTypeConstants.SHAPE_TYPE_DEPENDENCY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.myuml.myassociation.MyAssociation;
import plugins.vpcli.domain.myuml.myassociation.MyAssociationEnd;
import plugins.vpcli.domain.myuml.myassociation.MyAssociationEndFactory;
import plugins.vpcli.domain.myuml.myassociation.MyAssociationFactory;
import plugins.vpcli.domain.myuml.myclassifier.MyClassFactory;
import plugins.vpcli.domain.myuml.mydependency.MyDependency;
import plugins.vpcli.domain.myuml.mydependency.MyDependencyFactory;
import plugins.vpcli.domain.myuml.mygeneralization.MyGeneralizationFactory;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElement_old2;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElementFactoryFactory;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElementPool;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElementType;
import plugins.vpcli.domain.myuml.mydiagram.mydiagramelement.DiagramElementType;
import plugins.vpcli.domain.myuml.mydiagram.mydiagramelement.MyDiagramElementFactory;
import plugins.vpcli.domain.myuml.mydiagram.mydiagramelement.ProperMyDiagramElement;
import plugins.vpcli.domain.myuml.mymodel.MyModelFactory;
import plugins.vpcli.domain.myuml_old.mypackage_old.MyPackageFactory;
import plugins.vpcli.domain.myuml.mymodelrelationshipcontainer.MyModelRelationshipContainerFactory;
import plugins.vpcli.domain.myuml.myproject.MyProject;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotypeFactory;
import plugins.vpcli.domain.myuml.myunknownelement.UnimplementedElementFactory;
import plugins.vpcli.domain.myuml_old.myusecase_old.MyUseCaseFactory;

import com.vp.plugin.diagram.IConnectorUIModel;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IDependency;
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
            new MyAssociationEndFactory(pool),
            new MyDependencyFactory(pool),
            new MyGeneralizationFactory(pool),
            new MyModelRelationshipContainerFactory(pool),
            new MyStereotypeFactory(pool),
            new UnimplementedElementFactory(pool)
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
        IAssociationEnd vpFromEnd = mock(IAssociationEnd.class);
        when(vpFromEnd.getModelType()).thenReturn(IModelElementFactory.MODEL_TYPE_ASSOCIATION_END);
        when(vpFromEnd.getId()).thenReturn(connectorId + "-fe");
        when(vpFromEnd.getName()).thenReturn("");
        when(vpFromEnd.getDescription()).thenReturn("");
        when(vpFromEnd.getModelElement()).thenReturn(vpFrom);
        when(vpFromEnd.getMultiplicity()).thenReturn("");
        when(vpFromEnd.getNavigable()).thenReturn(MyAssociationEnd.NAVIGABLE_NAVIGABLE);

        IAssociationEnd vpToEnd = mock(IAssociationEnd.class);
        when(vpToEnd.getModelType()).thenReturn(IModelElementFactory.MODEL_TYPE_ASSOCIATION_END);
        when(vpToEnd.getId()).thenReturn(connectorId + "-te");
        when(vpToEnd.getName()).thenReturn("");
        when(vpToEnd.getDescription()).thenReturn("");
        when(vpToEnd.getModelElement()).thenReturn(vpTo);
        when(vpToEnd.getMultiplicity()).thenReturn("");
        when(vpToEnd.getNavigable()).thenReturn(MyAssociationEnd.NAVIGABLE_NON_NAVIGABLE);

        IAssociation association = mock(IAssociation.class);
        when(association.getModelType()).thenReturn(IModelElementFactory.MODEL_TYPE_ASSOCIATION);
        when(association.getId()).thenReturn(connectorId);
        when(association.getName()).thenReturn("");
        when(association.getDescription()).thenReturn("");
        when(association.getFromEnd()).thenReturn(vpFromEnd);
        when(association.getToEnd()).thenReturn(vpToEnd);

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

        MyElement_old2 modelElement = edge.getModelElement();
        assertEquals(MyElementType.ASSOCIATION, modelElement.getElementType());
        assertTrue(modelElement instanceof MyAssociation);

        MyAssociation association = (MyAssociation) modelElement;
        assertEquals("订单", association.getFromElement().getName());
        assertEquals(MyElementType.CLASS, association.getFromElement().getElementType());
        assertEquals("客户", association.getToElement().getName());
        assertEquals(MyElementType.CLASS, association.getToElement().getElementType());
    }

    private IConnectorUIModel mockDependencyConnector(
        IModelElement vpFrom, IModelElement vpTo, String connectorId
    ) {
        IDependency dependency = mock(IDependency.class);
        when(dependency.getModelType()).thenReturn(IModelElementFactory.MODEL_TYPE_DEPENDENCY);
        when(dependency.getId()).thenReturn(connectorId);
        when(dependency.getName()).thenReturn("");
        when(dependency.getDescription()).thenReturn("");
        when(dependency.getFrom()).thenReturn(vpFrom);
        when(dependency.getTo()).thenReturn(vpTo);

        IConnectorUIModel connector = mock(IConnectorUIModel.class);
        when(connector.getShapeType()).thenReturn(SHAPE_TYPE_DEPENDENCY);
        when(connector.getId()).thenReturn(connectorId + "-connector");
        when(connector.getModelElement()).thenReturn(dependency);
        return connector;
    }

    @Test
    void shouldCollectDependencyConnectorIntoSubEdges() {
        IShapeUIModel classAShape = mockClassShape("c1", "订单");
        IShapeUIModel classBShape = mockClassShape("c2", "客户");

        IConnectorUIModel dependencyConnector =
            mockDependencyConnector(classAShape.getModelElement(), classBShape.getModelElement(), "d1");

        IDiagramUIModel vpDiagram = mock(IDiagramUIModel.class);
        when(vpDiagram.getId()).thenReturn("dia1");
        when(vpDiagram.getName()).thenReturn("类图");
        when(vpDiagram.getDocumentation()).thenReturn("");
        when(vpDiagram.getType()).thenReturn("ClassDiagram");
        when(vpDiagram.toShapeUIModelArray()).thenReturn(new IShapeUIModel[]{classAShape, classBShape});
        when(vpDiagram.toConnectorUIModelArray()).thenReturn(new IConnectorUIModel[]{dependencyConnector});

        MyDiagram result = buildFactory().of(vpDiagram, root);

        assertEquals(2, result.getSubShapes().size());
        assertEquals(1, result.getSubEdges().size());

        ProperMyDiagramElement edge = result.getSubEdges().get(0);
        assertEquals(DiagramElementType.DEPENDENCY, edge.getDiagramElementType());
        assertTrue(edge.isEdge());
        assertFalse(edge.isShape());

        MyElement_old2 modelElement = edge.getModelElement();
        assertEquals(MyElementType.DEPENDENCY, modelElement.getElementType());
        assertTrue(modelElement instanceof MyDependency);

        MyDependency dependency = (MyDependency) modelElement;
        assertEquals("订单", dependency.getFrom().getName());
        assertEquals("客户", dependency.getTo().getName());
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
