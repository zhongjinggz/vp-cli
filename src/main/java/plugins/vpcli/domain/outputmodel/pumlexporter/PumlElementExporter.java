package plugins.vpcli.domain.outputmodel.pumlexporter;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IStereotype;
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

    // TODO make it static ?
    protected String getPumlAlias(IModelElement vpElement) {
        List<String> segments = new ArrayList<>();
        collectQualifiedNameSegments(segments, vpElement);
        return buildAliasFromSegments(segments.toArray(new String[0]));
    }

    private static void collectQualifiedNameSegments(List<String> segments, IModelElement element) {
        if (element == null) {
            return;
        }
        collectQualifiedNameSegments(segments, element.getParent());
        String name = element.getName();
        if (name == null || name.isEmpty()) {
            name = element.getId();
        }
        if (name != null && !name.isEmpty()) {
            segments.add(name);
        }
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

    public String getStereotypes(IModelElement vpElement) {
        Strings.StringPatcher result = Strings.buildStringPatcher();

        IStereotype[] stereotypes = vpElement.toStereotypeModelArray();
        if (stereotypes != null) {
            for (IStereotype stereotype : stereotypes) {
                result.patch("<<" + stereotype.getName() + ">>");
            }
        }
        return result.toString();
    }

}
