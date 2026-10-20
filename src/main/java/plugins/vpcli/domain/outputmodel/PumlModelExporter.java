package plugins.vpcli.domain.outputmodel;

import plugins.vpcli.domain.myuml.mycommon.MyElementTypeRepository;
import plugins.vpcli.domain.myuml.mycommon.myelementtype.MyElementTypeModel;
import plugins.vpcli.domain.myuml.mydiagram.ProperMyDiagramElement;

public class PumlModelExporter extends PumlElementExporter {
    public PumlModelExporter(
       // MyElementTypeRepository myElementTypeRepository
//        , PumlElementExporterFactory pumlElementExporterFactory
    ) {
//        super(myElementTypeRepository, MyElementTypeModel.class
////            , pumlElementExporterFactory
//        );
    }

    @Override
    public void to(PumlDiagram result, ProperMyDiagramElement myDiagramElement) {
        var myElement = myDiagramElement.getModelElement();
        result.beginBlock("namespace " + myElement.getName() +" <<model>> ");
        for (var aSubShape : myDiagramElement.getSubShapes()) {
//            var exporter = aSubShape.getModelElement().getMyElementType().getPumlElementExporter();
            var exporter = pumlElementExporterFactory.of( aSubShape.getModelElement().getElementType());
            exporter.to(result, aSubShape);
        }
        result.endBlock();

    }
}
