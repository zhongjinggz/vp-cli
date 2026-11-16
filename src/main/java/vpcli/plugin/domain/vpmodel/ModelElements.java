package vpcli.plugin.domain.vpmodel;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.diagram.shape.IClassUIModel;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IStereotype;
import org.jspecify.annotations.NonNull;
import vpcli.plugin.util.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModelElements {
    private ModelElements() {}

    public static String getStereotypesText(IModelElement vpElement) {
        Strings.StringPatcher result = Strings.buildStringPatcher();

        List<String> stereotypeNames = getStereotypeNames(vpElement);

        for (var aName : stereotypeNames) {
            result.patch("<<" + aName + ">>");
        }

        return result.toString();
    }

    public static @NonNull List<String> getStereotypeNames(IModelElement vpElement) {
        List<String> result = new ArrayList<>();

        IStereotype[] stereotypes = vpElement.toStereotypeModelArray();
        if (stereotypes != null) {
            for (IStereotype aStereotype : stereotypes) {
                result.add(aStereotype.getName());
            }
        }
        return result;
    }

    public static boolean hasStereotype(IModelElement element, String stereotypeName) {
        return getStereotypeNames(element).contains(stereotypeName);
    }
}
