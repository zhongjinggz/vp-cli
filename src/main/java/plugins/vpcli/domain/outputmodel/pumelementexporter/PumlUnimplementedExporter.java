package plugins.vpcli.domain.outputmodel.pumelementexporter;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plugins.vpcli.domain.myuml.mydiagram.mydiagramelement.ProperMyDiagramElement;
import plugins.vpcli.domain.outputmodel.PumlDiagram;

@Singleton
public class PumlUnimplementedExporter extends PumlElementExporter {
    Logger log = LoggerFactory.getLogger(PumlUnimplementedExporter.class);


    @Inject
    public PumlUnimplementedExporter() {
    }

    public void to(PumlDiagram result, ProperMyDiagramElement myDiagramElement) {
        log.warn(
            "Unimplemented element will not be exported, name={} type ={}",
            myDiagramElement.getModelElement().getName(),
            myDiagramElement.getModelElement().getElementType()
        );
    }
}
