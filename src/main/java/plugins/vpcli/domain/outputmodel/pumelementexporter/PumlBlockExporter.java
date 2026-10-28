package plugins.vpcli.domain.outputmodel.pumelementexporter;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IStereotype;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plugins.vpcli.domain.outputmodel.PumlDiagram;

import java.util.Iterator;

public abstract class PumlBlockExporter extends PumlElementExporter {

    private static final Logger logger = LoggerFactory.getLogger(PumlBlockExporter.class);

    public PumlBlockExporter() {
    }

    @Override
    public void export(PumlDiagram result, IDiagramElement vpDiagramElement) {
        IModelElement vpElement = vpDiagramElement.getModelElement();

        result.beginBlock(
            buildBlockType(vpElement),
            vpElement.getName(),
            getPumlAlias(vpElement),
            buildBlockSuffix(vpElement)
        );

        writeBlockBody(result, vpElement);

        if (vpDiagramElement instanceof IShapeUIModel) {
            IShapeUIModel shape = (IShapeUIModel) vpDiagramElement;
            for (IDiagramElement child : shape.toChildArray()) {
                if (child.getModelElement() == null) {
                    continue;
                }
                var exporter = pumlElementExporterFactory.build(child.getShapeType());
                exporter.export(result, child);
            }
        }
        result.endBlock();
    }

    abstract String getBlockType();

    // 供需要在块声明末尾追加内容（如 <<model>> 原型）的子类覆写
    String getKeyword() {
        return "";
    }

    /** 步 #15：块声明类型；默认取 getBlockType()，子类（如类需区分 abstract）可覆写。 */
    String buildBlockType(IModelElement vpElement) {
        return getBlockType();
    }

    /**
     * 步 #15：块声明后缀 = 固定原型（getKeyword()）+ 元素上施加的泛型。
     * 组合顺序：固定原型在前，随后是每个泛型。
     */
    String buildBlockSuffix(IModelElement vpElement) {
        String fixed = getKeyword();
        String stereotypes = renderStereotypes(vpElement);
        if (fixed.isEmpty()) {
            return stereotypes;
        }
        if (stereotypes.isEmpty()) {
            return fixed;
        }
        return fixed + " " + stereotypes;
    }

    /** 步 #15：读取元素上施加的泛型名，原样输出为「<<名>>」，多项用单个空格分隔；无则返回空串。 */
    String renderStereotypes(IModelElement vpElement) {
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

    // 步 A7：VP 原生读法的成员区钩子；默认空实现，子类按需覆写
    void writeBlockBody(PumlDiagram result, IModelElement vpElement) {
    }
}
