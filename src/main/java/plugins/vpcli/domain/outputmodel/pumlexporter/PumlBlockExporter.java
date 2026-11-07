package plugins.vpcli.domain.outputmodel.pumlexporter;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlDiagram;

import static plugins.vpcli.util.Strings.buildStringPatcher;

public abstract class PumlBlockExporter extends PumlElementExporter {

    public PumlBlockExporter() {
    }

    @Override
    public void export(PumlDiagram result, IDiagramElement vpDiagramElement) {
        // example: class "abc" as package1_abc <<stereotype1>> <<stereotype2>>
        result.beginBlock(buildHeader(vpDiagramElement));

        exportBlockBody(result, vpDiagramElement);

        result.endBlock();
    }

    protected String buildHeader(IDiagramElement vpDiagramElement) {
        IModelElement vpElement = vpDiagramElement.getModelElement();
        return buildStringPatcher(getBlockType(vpElement))
            .patch("\"" + vpElement.getName() + "\"")
            .patch("as")
            .patch(pumlAlias(vpDiagramElement))
            .patch(templateParams(vpElement))
            .patch(keywordAndStereotypes(vpElement))
            .toString();
    }

    // 供需要在块声明末尾追加 keyword（如 <<model>> ）的子类覆写
    public String getKeyword() {
        return "";
    }

    public abstract String getBlockType(IModelElement vpElement);

    /**
     * 组合顺序：<<keyword>> <<stereotype1>> <<stereotype2>>
     */
    public String keywordAndStereotypes(IModelElement vpElement) {
        return buildStringPatcher(getKeyword())
            .patch(getStereotypesText(vpElement))
            .toString();
    }

    protected String templateParams(IModelElement vpElement){
        return "";
    }

    public void exportBlockBody(PumlDiagram result, IDiagramElement vpDiagramElement) {
        if (vpDiagramElement instanceof IShapeUIModel) {
            IShapeUIModel shape = (IShapeUIModel) vpDiagramElement;
            for (IDiagramElement child : shape.toChildArray()) {
                if (child.getModelElement() != null) {
                    var exporter = exporterRepository.findBy(child);
                    exporter.export(result, child);
                }
            }
        }
    }
}
