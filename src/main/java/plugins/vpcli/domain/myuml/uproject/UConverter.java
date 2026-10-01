package plugins.vpcli.domain.myuml.uproject;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.factory.IModelElementFactory;
import plugins.vpcli.application.TreeService;
import plugins.vpcli.domain.myuml.upackage.UPackage;

import java.util.ArrayList;
import java.util.List;

public class UConverter {
    public List<UPackage> fromVPElementsToMyPackages(IModelElement[] vpElements, TreeService treeService) {
        List<UPackage> result = new ArrayList<>();
        for (var aVPElement : vpElements) {
            if (isPackage(aVPElement)) {
                var aPackage = fromVPElement(aVPElement);
                var subPackages = fromVPElementsToMyPackages(aVPElement.toChildArray(), treeService);
                aPackage.addChildren(subPackages);
                result.add(aPackage);
            }
        }
        return result;
    }

    boolean isPackage(IModelElement element) {
        String type = element.getModelType();
        return IModelElementFactory.MODEL_TYPE_PACKAGE.equals(type)
            || IModelElementFactory.MODEL_TYPE_MODEL.equals(type);
    }

    UPackage fromVPElement(IModelElement vpElement) {
        return new UPackage(vpElement.getId(), vpElement.getName());
    }
}
