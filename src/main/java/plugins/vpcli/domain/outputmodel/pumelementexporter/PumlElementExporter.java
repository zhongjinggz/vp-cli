package plugins.vpcli.domain.outputmodel.pumelementexporter;

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
