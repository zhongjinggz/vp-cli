package plugins.vpcli.domain.outputmodel.pumelementexporter;

import com.vp.plugin.diagram.IDiagramElement;
import plugins.vpcli.domain.myuml.mydiagram.mydiagramelement.ProperMyDiagramElement;
import plugins.vpcli.domain.myuml.myelement.MyElement;
import plugins.vpcli.domain.outputmodel.PumlDiagram;

public abstract class PumlElementExporter {
    protected PumlElementExporterFactory pumlElementExporterFactory;

    public PumlElementExporter() {
    }

    // used by PumlElementExporterFactory to build the bi-direction dependency, which is necessary
    public void setExporterFactory(PumlElementExporterFactory pumlElementExporterFactory) {
        this.pumlElementExporterFactory = pumlElementExporterFactory;
    }

    abstract public void to(PumlDiagram result, ProperMyDiagramElement myDiagramElement);

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
        to(result, myDiagramElement);
    }

    /**
     * PlantUML 别名：将元素全限定名各段清洗（仅保留各语言字母和数字，其余转 "_"）后，
     * 以 "__" 连接（对应全限定名中的 "."），保证别名唯一、可与元素一一对应。
     */
    protected String getPumlAlias(MyElement myElement) {
        StringBuilder sb = new StringBuilder();
        for (String segment : myElement.getQualifiedName().split("\\.")) {
            if (sb.length() > 0) {
                sb.append("__");
            }
            sb.append(segment.replaceAll("[^\\p{L}\\p{N}]", "_"));
        }
        return sb.toString();
    }
}
