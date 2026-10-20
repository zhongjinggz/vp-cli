package plugins.vpcli.domain.outputmodel;

import plugins.vpcli.domain.myuml.mydiagram.ProperMyDiagramElement;

public abstract class PumlElementExporter {
    protected PumlElementExporterFactory pumlElementExporterFactory;

    public PumlElementExporter() {

    }

    // used by PumlElementExporterFactory to build the bi-direction dependency, which is necessary
    public void setExporterFactory(PumlElementExporterFactory pumlElementExporterFactory) {
        this.pumlElementExporterFactory = pumlElementExporterFactory;
    }

    abstract void to(PumlDiagram result, ProperMyDiagramElement myDiagramElement);
}
