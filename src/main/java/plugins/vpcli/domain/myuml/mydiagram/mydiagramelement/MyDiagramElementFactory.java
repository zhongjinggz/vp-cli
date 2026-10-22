package plugins.vpcli.domain.myuml.mydiagram.mydiagramelement;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.model.IModelElement;
import org.jetbrains.annotations.NotNull;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactoryFactory;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class MyDiagramElementFactory {
    private final MyElementFactoryFactory buildMyElementFactory;

    @Inject
    public MyDiagramElementFactory(
        MyElementFactoryFactory myElementFactoryFactory
//        , MyDiagramElementCreator myDiagramElementCreator
    ) {
        this.buildMyElementFactory = myElementFactoryFactory;
    }

    public void ofShape(IDiagramElement vpDiagramElement,
                        MyDiagramElement parentDiagramElement
    ) {
        var result = buildRoot(vpDiagramElement, parentDiagramElement);
        buildSubElements(vpDiagramElement, result);
    }

    /**
     * 连接线（connector）没有子 shape，因此只构建根元素、不递归子节点。
     * 其 modelElement 类型为关联时，会自动经 MyAssociationFactory 生成带端点的 MyAssociation。
     */
    public void ofConnector(
        IDiagramElement vpConnector,
        MyDiagramElement parentDiagramElement
    ) {
        buildRoot(vpConnector, parentDiagramElement);
    }

    private void buildSubElements(IDiagramElement vpDiagramElement, ProperMyDiagramElement result) {
        var subShapes = vpDiagramElement.toChildArray();
        for (var aShape : subShapes) {
            this.ofShape(aShape, result);
        }
    }

    private @NotNull ProperMyDiagramElement buildRoot(IDiagramElement vpDiagramElement, MyDiagramElement parentDiagramElement) {
        IModelElement vpElement = vpDiagramElement.getModelElement();

        var buildMyElement = buildMyElementFactory.of(vpElement.getModelType());

        var myElement = buildMyElement.of(vpElement);

        var result = createMyDiagramElement(vpDiagramElement, parentDiagramElement, myElement);
        return result;
    }

    private @NotNull ProperMyDiagramElement createMyDiagramElement(
        IDiagramElement vpDiagramElement,
        MyDiagramElement parentDiagramElement,
        MyElement myElement
    ) {
        return new ProperMyDiagramElement(
            vpDiagramElement.getId(),
            DiagramElementType.of(vpDiagramElement.getShapeType()),
            myElement,
            parentDiagramElement
        );
    }
}
