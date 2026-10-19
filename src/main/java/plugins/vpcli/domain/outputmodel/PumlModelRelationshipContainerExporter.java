package plugins.vpcli.domain.outputmodel;

import plugins.vpcli.domain.myuml.mycommon.MyElementTypeRepository;
import plugins.vpcli.domain.myuml.mycommon.myelementtype.MyElementTypeModelRelationshipContainer;
import plugins.vpcli.domain.myuml.mydiagram.ProperMyDiagramElement;

public class PumlModelRelationshipContainerExporter extends PumlElementExporter {


    public PumlModelRelationshipContainerExporter(MyElementTypeRepository myElementTypeRepository) {
        super(myElementTypeRepository, MyElementTypeModelRelationshipContainer.class);
    }

    public void to(PumlDiagram result, ProperMyDiagramElement myDiagramElement) {
        // it seems this will never been called. remove it later. Use Null Object pattern?

    }
}
