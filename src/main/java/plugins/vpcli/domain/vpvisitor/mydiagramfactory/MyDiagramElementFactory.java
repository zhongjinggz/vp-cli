package plugins.vpcli.domain.vpvisitor.mydiagramfactory;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.model.IModelElement;
import org.jetbrains.annotations.NotNull;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactoryFactory;
import plugins.vpcli.domain.myuml.mydiagram.*;

public class MyDiagramElementFactory {
    private final MyElementFactoryFactory myElementFactoryFactory;
    private final MyDiagramElementCreator createMyDiagramElement;

    public MyDiagramElementFactory(
        MyElementFactoryFactory myElementFactoryFactory,
        MyDiagramElementCreator myDiagramElementCreator
    ) {
        this.myElementFactoryFactory = myElementFactoryFactory;
        this.createMyDiagramElement = myDiagramElementCreator;
    }

    public MyDiagramElement of(IDiagramElement vpDiagramElement,
                               MyDiagramElement parentDiagramElement) {

        var result = buildRoot(vpDiagramElement, parentDiagramElement);
        buildSubElements(vpDiagramElement, result);

        return result;
    }

    private void buildSubElements(IDiagramElement vpDiagramElement, ProperMyDiagramElement result) {
        var subShapes = vpDiagramElement.toChildArray();
        for (var aShape : subShapes) {
            this.of(aShape, result);
        }
    }

    private @NotNull ProperMyDiagramElement buildRoot(IDiagramElement vpDiagramElement, MyDiagramElement parentDiagramElement) {
        IModelElement vpElement = vpDiagramElement.getModelElement();

        MyElementFactory buildMyElement = myElementFactoryFactory.of(
            vpElement.getModelType()
        );

        var myElement = buildMyElement.of(vpElement);

        var result = createMyDiagramElement.of(
            vpDiagramElement,
            parentDiagramElement,
            myElement
        );
        return result;
    }
}
