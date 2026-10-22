package plugins.vpcli.domain.outputmodel.pumelementexporter;

import jakarta.inject.Inject;
import org.slf4j.Logger;
import plugins.vpcli.domain.myuml.mydiagram.mydiagramelement.ProperMyDiagramElement;
import plugins.vpcli.domain.outputmodel.PumlDiagram;
import plugins.vpcli.drivenadapter.DocWriter;
import plugins.vpcli.util.MyIO;

import static org.slf4j.LoggerFactory.getLogger;

public abstract class PumlBlockExporter extends PumlElementExporter {

    private final Logger logger;

    public PumlBlockExporter() {
        this.logger = getLogger(PumlBlockExporter.class);
    }

    @Override
    public void to(PumlDiagram result, ProperMyDiagramElement myDiagramElement) {
        var myElement = myDiagramElement.getModelElement();

        logger.debug("exporting block: elementType={} name={} alias={} suffix={}",
            myElement.getElementType(),
            myElement.getName(),
            getPumlAlias(myElement),
            getBlockSuffix()
        );

//        result.beginBlock(getBlockType() + " \"" + myElement.getName() + "\" as " + getPumlAlias(myElement) + getBlockSuffix());
        result.beginBlock(
            getBlockType(),
            myElement.getName(),
            getPumlAlias(myElement),
            getBlockSuffix()
        );

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
