package plugins.vpcli.domain.outputmodel;

import plugins.vpcli.domain.myuml.mydiagram.ProperMyDiagramElement;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class PumlPackageExporter extends PumlElementExporter {

    @Inject
    public PumlPackageExporter(
       // MyElementTypeRepository myElementTypeRepository
//        , PumlElementExporterFactory pumlElementExporterFactory
    ) {
//        super(myElementTypeRepository, MyElementTypePackage.class
////            , pumlElementExporterFactory
//        );
    }

    @Override
    public void to(PumlDiagram result, ProperMyDiagramElement myDiagramElement) {
        var myElement = myDiagramElement.getModelElement();
        result.beginBlock("namespace " + myElement.getName());
        for (var aSubShape : myDiagramElement.getSubShapes()) {
//            var exporter = aSubShape.getModelElement().getMyElementType().getPumlElementExporter();
            var exporter = pumlElementExporterFactory.of( aSubShape.getModelElement().getElementType());
            exporter.to(result, aSubShape);
        }
        result.endBlock();

    }
}
