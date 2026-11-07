package plugins.vpcli.domain.outputmodel.pumlexporter;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IStereotype;
import org.jspecify.annotations.NonNull;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlDiagram;
import plugins.vpcli.util.Strings;

import java.util.ArrayList;
import java.util.List;

public abstract class PumlElementExporter {
    protected PumlElementExporterRepository exporterRepository;

    public PumlElementExporter() {
    }

    // used by PumlElementExporterRepository to build the bi-direction dependency
    public void setExporterRepository(PumlElementExporterRepository pumlElementExporterRepository) {
        this.exporterRepository = pumlElementExporterRepository;
    }

    abstract public String getShapeType();

    public void export(PumlDiagram result, IDiagramElement vpDiagramElement) {
    }

    // 别名 := <类型>__<名称>__<模型ID>__<图ID>，各段以 __ 分隔。
    // 无名元素忽略名称段；图元素 ID 为 null 时省略图ID段。
    public static String pumlAlias(IDiagramElement vpDiagramElement) {

        List<String> segments = new ArrayList<>();

        var modelElement = vpDiagramElement.getModelElement();
        var diagramElementId = vpDiagramElement.getId();

        String type = modelElement == null ? null : modelElement.getModelType();
        if (type != null && !type.isEmpty()) {
            segments.add(type);
        }
        String name = modelElement == null ? null : modelElement.getName();
        if (name != null && !name.isEmpty()) {
            segments.add(name);
        }
        String modelId = modelElement == null ? null : modelElement.getId();
        if (modelId != null && !modelId.isEmpty()) {
            segments.add(modelId);
        }
        if (diagramElementId != null && !diagramElementId.isEmpty()) {
            segments.add(diagramElementId);
        }
        return buildAliasFromSegments(segments.toArray(new String[0]));
    }

    private static String buildAliasFromSegments(String[] segments) {
        StringBuilder sb = new StringBuilder();
        for (String segment : segments) {
            if (sb.length() > 0) {
                sb.append("__");
            }
            sb.append(segment.replaceAll("[^\\p{L}\\p{N}]", "_"));
        }
        return sb.toString();
    }

    public String getStereotypesText(IModelElement vpElement) {
        Strings.StringPatcher result = Strings.buildStringPatcher();

        List<String> stereotypeNames = getStereotypeNames(vpElement);

        for (var aName : stereotypeNames) {
            result.patch("<<" + aName + ">>");
        }

        return result.toString();
    }

    protected static @NonNull List<String> getStereotypeNames(IModelElement vpElement) {
        List<String> result = new ArrayList<>();

        IStereotype[] stereotypes = vpElement.toStereotypeModelArray();
        if (stereotypes != null) {
            for (IStereotype aStereotype : stereotypes) {
                result.add(aStereotype.getName());
            }
        }
        return result;
    }

}
