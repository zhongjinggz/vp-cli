package plugins.vpcli.domain.outputmodel.pumlexporter;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IStereotype;
import org.jetbrains.annotations.NotNull;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlDiagram;

import java.util.ArrayList;
import java.util.Iterator;
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

    public String renderStereotypes(IModelElement vpElement) {
        StringBuilder sb = new StringBuilder();
        Iterator<?> it = vpElement.stereotypeModelIterator();
        while (it.hasNext()) {
            IStereotype stereotype = (IStereotype) it.next();
            String name = stereotype.getName();
            if (name == null || name.isEmpty()) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append("<<").append(name).append(">>");
        }
        return sb.toString();
    }

    protected String buildEdgeLabel(String name, IModelElement vpElement) {
        String stereotypes = renderStereotypes(vpElement);
        if (name == null || name.isEmpty()) {
            return stereotypes;
        }
        if (stereotypes.isEmpty()) {
            return name;
        }
        return name + " " + stereotypes;
    }

    protected static List<String> getStereotypes(@NotNull IModelElement vpElement) {
        List<String> result = new ArrayList<>();
        IStereotype[] stereotypes = vpElement.toStereotypeModelArray();
        if (stereotypes != null) {
            for (IStereotype stereotype : stereotypes) {
                result.add(stereotype.getName());
            }
        }
        return result;
    }

}
