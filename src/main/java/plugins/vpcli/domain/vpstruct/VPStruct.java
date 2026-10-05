package plugins.vpcli.domain.vpstruct;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;

import java.util.Arrays;

public class VPStruct {
    private final IProject project;
    private final VPStructElementFactory createStructElement;
    private VPVisitorFactory visitorFactory;
    private String[] elementTypes = new String[]{};

    public VPStruct(IProject vpProject, VPStructElementFactory elementFactory) {
        this.project = vpProject;
        this.createStructElement = elementFactory;
    }

    public void setVisitorFactory(VPVisitorFactory treeVisitorFactory) {
        this.visitorFactory = treeVisitorFactory;
    }

    public void setElementTypes(String[] elementTypes) {
        this.elementTypes = Arrays.copyOf(elementTypes, elementTypes.length);
    }

    public void accept() {
        var topLevelElements = project.toModelElementArray(elementTypes);
        elementsAccept(topLevelElements, null);
    }

    private void elementsAccept(IModelElement[] vpElements, VPVisitor preLevelVisitor) {

        for (var aVPElement : vpElements) {
            var visitor = visitorFactory.create(preLevelVisitor);
            var structElement = createStructElement.from(aVPElement);
            visitor.visit(structElement);

            elementsAccept(
                aVPElement.toChildArray(elementTypes)
                , visitor
            );
        }
    }
}
