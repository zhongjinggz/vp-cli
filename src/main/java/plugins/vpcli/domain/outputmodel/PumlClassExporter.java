package plugins.vpcli.domain.outputmodel;

import plugins.vpcli.domain.myuml.mycommon.MyElementTypeRepository;
import plugins.vpcli.domain.myuml.mycommon.myelementtype.MyElementTypeClass;
import plugins.vpcli.domain.myuml.mydiagram.ProperMyDiagramElement;

public class PumlClassExporter extends PumlElementExporter {
    public PumlClassExporter(MyElementTypeRepository myElementTypeRepository
//        , PumlElementExporterFactory pumlElementExporterFactory
    ) {
//        super(myElementTypeRepository, MyElementTypeClass.class
////            , pumlElementExporterFactory
//        ) ;
    }

    public void to(PumlDiagram result, ProperMyDiagramElement myDiagramElement) {
        var myElement = myDiagramElement.getModelElement();
        result.beginBlock("class " + myElement.getName());
        for (var aSubShape : myDiagramElement.getSubShapes()) {
//            var exporter = aSubShape.getModelElement().getMyElementType().getPumlElementExporter();
            var exporter = pumlElementExporterFactory.of( aSubShape.getModelElement().getElementType());
            exporter.to(result, aSubShape);
        }
        result.endBlock();

    }
}
