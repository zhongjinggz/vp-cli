package plugins.vpcli.domain.outputmodel.pumelementexporter;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.mydiagram.mydiagramelement.ProperMyDiagramElement;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElement_old2;
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

//    public void to(PumlDiagram result, ProperMyDiagramElement myDiagramElement) {
//    }

    /**
     * 步 A3：返回该 exporter 所代表的 VP 形状类型字符串（单一来源）。
     * 供 {@link PumlElementExporterFactory} 以 VP 形状常量为 key 分发；
     * 依赖《com.vp.plugin.diagram.IShapeTypeConstants》（注意接口名为 Constants 而非 Consts）。
     */
    abstract public String getShapeType();

    /**
     * 过渡入口（步 A1）：在导出器直读 VP 原生模型前，先铺好"接收对应 VP 图形元素"的通路。
     * ProperMyDiagramElement 是基于 VP 的 IDiagramElement 构造的，故此处下发 IDiagramElement。
     * 当前只是透传 VP 元素却不消费——直接委托回既有读法，保证此刻输出字节级等价。
     * 后续每迁移一个子类，才在其内部改写为直读 vpDiagramElement。
     */
    public void to(PumlDiagram result, ProperMyDiagramElement myDiagramElement, IDiagramElement vpDiagramElement) {
        to(result, myDiagramElement, vpDiagramElement);
    }

    /**
     * PlantUML 别名：将元素全限定名各段清洗（仅保留各语言字母和数字，其余转 "_"）后，
     * 以 "__" 连接（对应全限定名中的 "."），保证别名唯一、可与元素一一对应。
     */
    protected String getPumlAlias(MyElement_old2 myElement) {
        return buildAliasFromSegments(myElement.getQualifiedName().split("\\."));
    }

    /**
     * 步 A4：别名改读 VP 原生模型。VP 的 {@link IModelElement} 无现成的 getQualifiedName()，
     * 故沿 getParent() 链自上而下收集各层名字，以 "." 连接（跳过空名；Project 根不属于
     * 该链），再复用与 {@link #getPumlAlias(MyElement_old2)} 相同的清洗/连接算法。
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
