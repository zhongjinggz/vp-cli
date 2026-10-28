package plugins.vpcli.domain.outputmodel.pumelementexporter;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IStereotype;
import plugins.vpcli.domain.outputmodel.PumlDiagram;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class PumlElementExporter {
    protected PumlElementExporterFactory pumlElementExporterFactory;

    public PumlElementExporter() {
    }

    // used by PumlElementExporterFactory to build the bi-direction dependency, which is necessary
    public void setExporterFactory(PumlElementExporterFactory pumlElementExporterFactory) {
        this.pumlElementExporterFactory = pumlElementExporterFactory;
    }

    /**
     * 步 A3：返回该 exporter 所代表的 VP 形状类型字符串（单一来源）。
     * 供 {@link PumlElementExporterFactory} 以 VP 形状常量为 key 分发；
     * 依赖《com.vp.plugin.diagram.IShapeTypeConstants》（注意接口名为 Constants 而非 Consts）。
     */
    abstract public String getShapeType();

    /**
     * 步 A1：导出入口。导出器直读 VP 原生模型——此处下发的即 VP 图形元素（IDiagramElement），
     * 各子类在内部直读其 IModelElement。
     */
    public void export(PumlDiagram result, IDiagramElement vpDiagramElement) {
    }

    /**
     * 步 A4：别名改读 VP 原生模型
     */
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

    /** 步 #16：读取元素上施加的泛型名，原样输出为「<<名>>」，多项用单个空格分隔；无则返回空串。 */
    protected String renderStereotypes(IModelElement vpElement) {
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

    /** 步 #16：把关系名称与所施加泛型合并为连线标签；两者皆空则返回空串。 */
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
}
