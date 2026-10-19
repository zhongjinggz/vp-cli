package plugins.vpcli.domain.outputmodel;

import plugins.vpcli.domain.myuml.mycommon.MyElementTypeRepository;
import plugins.vpcli.domain.myuml.mycommon.myelementtype.MyElementTypePackage;
import plugins.vpcli.domain.myuml.mydiagram.ProperMyDiagramElement;

public class PumlAssociationExporter extends PumlElementExporter {
    public PumlAssociationExporter(MyElementTypeRepository myElementTypeRepository) {
        super(myElementTypeRepository, MyElementTypePackage.class);
    }

    public void to(PumlDiagram result, ProperMyDiagramElement myDiagramElement) {
        var myElement = myDiagramElement.getModelElement();
        result.writeTerminalElement(" -- ");

    }
}
