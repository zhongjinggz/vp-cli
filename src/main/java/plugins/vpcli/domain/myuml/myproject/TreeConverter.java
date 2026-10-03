package plugins.vpcli.domain.myuml.myproject;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;
import plugins.vpcli.domain.myuml.mypackage.MyPackage;

import java.util.ArrayList;
import java.util.List;

public class TreeConverter {
    public List<MyPackage> fromVPElements(IModelElement[] vpElements
        , String[] vpModelTypes) {
        List<MyPackage> result = new ArrayList<>();
        for (var aElement : vpElements) {
            var aPackage = fromVPElement(aElement);
            var subPackages = fromVPElements(
                aElement.toChildArray(vpModelTypes)
                , vpModelTypes);
            aPackage.addChildren(subPackages);
            result.add(aPackage);
        }
        return result;
    }

    boolean isPackage(IModelElement element) {
        String type = element.getModelType();
        return IModelElementFactory.MODEL_TYPE_PACKAGE.equals(type)
            || IModelElementFactory.MODEL_TYPE_MODEL.equals(type);
    }

    MyPackage fromVPElement(IModelElement vpElement) {
        return new MyPackage(vpElement.getId(), vpElement.getName());
    }

    public List<MyPackage> fromVPProject(IProject project) {
        String[] vpModelTypes = {
            IModelElementFactory.MODEL_TYPE_PACKAGE
            , IModelElementFactory.MODEL_TYPE_MODEL
        };

        var topLevel = project.toModelElementArray(vpModelTypes);

        var packages = fromVPElements(topLevel, vpModelTypes);
        return packages;
    }
}
