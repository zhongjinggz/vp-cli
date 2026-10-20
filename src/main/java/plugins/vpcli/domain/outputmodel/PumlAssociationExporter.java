package plugins.vpcli.domain.outputmodel;

import plugins.vpcli.domain.myuml.mycommon.MyElementTypeRepository;
import plugins.vpcli.domain.myuml.mycommon.myelementtype.MyElementTypeAssociation;
import plugins.vpcli.domain.myuml.mydiagram.ProperMyDiagramElement;

public class PumlAssociationExporter extends PumlElementExporter {
    public PumlAssociationExporter(
//        MyElementTypeRepository myElementTypeRepository
//        , PumlElementExporterFactory pumlElementExporterFactory
    ) {
//        super(myElementTypeRepository, MyElementTypeAssociation.class
////            , pumlElementExporterFactory
//        );
    }

    public void to(PumlDiagram result, ProperMyDiagramElement myDiagramElement) {
        var myElement = myDiagramElement.getModelElement();
        result.writeTerminalElement(" -- ");

    }
}
