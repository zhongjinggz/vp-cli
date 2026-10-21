package plugins.vpcli.domain.outputmodel.pumelementexporter;

import plugins.vpcli.domain.myuml.mydiagram.mydiagramelement.ProperMyDiagramElement;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.outputmodel.PumlDiagram;

@Singleton
public class PumlUseCaseExporter extends PumlElementExporter {
    @Inject
    public PumlUseCaseExporter(
        //MyElementTypeRepository myElementTypeRepository
//        , PumlElementExporterFactory pumlElementExporterFactory
    ) {
//        super(myElementTypeRepository, MyElementTypeUseCase.class
////            , pumlElementExporterFactory
//        );
    }

    public void to(PumlDiagram result, ProperMyDiagramElement myDiagramElement) {
        var myElement = myDiagramElement.getModelElement();
        result.writeTerminalElement("usecase \"" + myElement.getName() + "\" as " + myElement.getPumlAlias());
    }
}
