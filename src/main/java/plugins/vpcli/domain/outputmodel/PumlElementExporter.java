package plugins.vpcli.domain.outputmodel;

import plugins.vpcli.domain.myuml.mycommon.MyElementTypeRepository;
import plugins.vpcli.domain.myuml.mycommon.myelementtype.MyElementType;
import plugins.vpcli.domain.myuml.mycommon.myelementtype.MyElementTypePackage;
import plugins.vpcli.domain.myuml.mydiagram.ProperMyDiagramElement;

public abstract class PumlElementExporter {

    private MyElementType myElementType;
    public PumlElementExporter(
        MyElementTypeRepository myElementTypeRepository,
        Class<? extends MyElementType> myElementTypePackageClass
    ) {
        myElementTypeRepository.register(this, myElementTypePackageClass);

    }

    abstract void to(PumlDiagram result, ProperMyDiagramElement myDiagramElement) ;
}
