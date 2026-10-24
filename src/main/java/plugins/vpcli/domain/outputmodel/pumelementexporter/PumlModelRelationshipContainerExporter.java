package plugins.vpcli.domain.outputmodel.pumelementexporter;

import plugins.vpcli.domain.myuml.mydiagram.mydiagramelement.ProperMyDiagramElement;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.outputmodel.PumlDiagram;

@Singleton
public class PumlModelRelationshipContainerExporter extends PumlElementExporter {


    @Inject
    public PumlModelRelationshipContainerExporter() {
    }

    public void to(PumlDiagram result, ProperMyDiagramElement myDiagramElement) {
        // it seems this will never been called. remove it later. Use Null Object pattern?

    }
}
