package plugins.vpcli.domain.outputmodel.pumelementexporter;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.outputmodel.PumlDiagram;

import java.util.ArrayList;
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
    public void to(PumlDiagram result, IDiagramElement vpDiagramElement) {
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
}
