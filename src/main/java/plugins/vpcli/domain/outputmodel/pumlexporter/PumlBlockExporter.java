package plugins.vpcli.domain.outputmodel.pumlexporter;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlDiagram;

public abstract class PumlBlockExporter extends PumlElementExporter {

    public PumlBlockExporter() {
    }

    @Override
    public void export(PumlDiagram result, IDiagramElement vpDiagramElement) {
        IModelElement vpElement = vpDiagramElement.getModelElement();

        result.beginBlock(
            getBlockType(vpElement),
            vpElement.getName(),
            getPumlAlias(vpElement),
            buildBlockSuffix(vpElement)
        );

        exportBlockBody(result, vpDiagramElement);

        result.endBlock();
    }

    // 供需要在块声明末尾追加 getKeyword（如 <<model>> 原型）的子类覆写
    public String getKeyword() {
        return "";
    }

    /**
     * 步 #15：块声明类型；默认取 getBlockType()，子类（如类需区分 abstract）可覆写。
     */
    public abstract String getBlockType(IModelElement vpElement);

    /**
     * 步 #15：块声明后缀 = getKeyword（getKeyword()）+ 元素上的泛型。
     * 组合顺序：getKeyword ，随后是每个泛型。
     */
    public String buildBlockSuffix(IModelElement vpElement) {
        String fixed = getKeyword();
        String stereotypes = getStereotypes(vpElement);
        if (fixed.isEmpty()) {
            return stereotypes;
        }
        if (stereotypes.isEmpty()) {
            return fixed;
        }
        return fixed + " " + stereotypes;
    }

    // 步 A7：VP 原生读法的成员区钩子；默认空实现，子类按需覆写
    public void exportBlockBody(PumlDiagram result, IDiagramElement vpDiagramElement) {
        if (vpDiagramElement instanceof IShapeUIModel) {
            IShapeUIModel shape = (IShapeUIModel) vpDiagramElement;
            for (IDiagramElement child : shape.toChildArray()) {
                if (child.getModelElement() != null) {
                    var exporter = exporterRepository.findByShapeType(child.getShapeType());
                    exporter.export(result, child);
                }
            }
        }
    }
}
