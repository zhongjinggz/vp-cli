package plugins.vpcli.domain.outputmodel.pumelementexporter;

import plugins.vpcli.domain.myuml.mydiagram.mydiagramelement.ProperMyDiagramElement;
import plugins.vpcli.domain.outputmodel.PumlDiagram;

public abstract class PumlBlockExporter extends PumlElementExporter {

    @Override
    public void to(PumlDiagram result, ProperMyDiagramElement myDiagramElement) {
        var myElement = myDiagramElement.getModelElement();
        result.beginBlock(getBlockType() + " \"" + myElement.getName() + "\" as " + getPumlAlias(myElement) + getBlockSuffix());
        for (var aSubShape : myDiagramElement.getSubShapes()) {
            var exporter = pumlElementExporterFactory.of(aSubShape.getModelElement().getElementType());
            exporter.to(result, aSubShape);
        }
        result.endBlock();

    }

    abstract String getBlockType();

    // 供需要在块声明末尾追加内容（如 <<model>> 原型）的子类覆写
    String getBlockSuffix() {
        return "";
    }
}
