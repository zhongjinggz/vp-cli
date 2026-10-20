package plugins.vpcli.domain.outputmodel;

import plugins.vpcli.domain.myuml.mycommon.MyElementTypeRepository;
import plugins.vpcli.domain.myuml.mycommon.myelementtype.MyElementType;
import plugins.vpcli.domain.myuml.mydiagram.ProperMyDiagramElement;

public abstract class PumlElementExporter {
    protected PumlElementExporterFactory pumlElementExporterFactory;

    private MyElementType myElementType;

    public PumlElementExporter(
        MyElementTypeRepository myElementTypeRepository,
        Class<? extends MyElementType> myElementTypePackageClass
//        , PumlElementExporterFactory pumlElementExporterFactory
    ) {
        myElementTypeRepository.register(this, myElementTypePackageClass);
//        this.pumlElementExporterFactory = pumlElementExporterFactory;

    }

    // used by PumlElementExporterFactory to build the bi-direction dependency, which is necessary
    public void setExporterFactory(PumlElementExporterFactory pumlElementExporterFactory) {
        this.pumlElementExporterFactory = pumlElementExporterFactory;
    }

    abstract void to(PumlDiagram result, ProperMyDiagramElement myDiagramElement);
}
