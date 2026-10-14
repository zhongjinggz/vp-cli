package plugins.vpcli.domain.outputmodel;

import plugins.vpcli.domain.myuml.mydiagram.ProperMyDiagramElement;

public interface PumlElementExporter {
    void to(PumlDiagram result, ProperMyDiagramElement myDiagramElement) ;
}
