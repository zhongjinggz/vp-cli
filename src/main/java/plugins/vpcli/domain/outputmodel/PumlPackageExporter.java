package plugins.vpcli.domain.outputmodel;

import plugins.vpcli.domain.myuml.mycommon.MyElementTypeRepository;
import plugins.vpcli.domain.myuml.mycommon.myelementtype.MyElementTypePackage;
import plugins.vpcli.domain.myuml.mydiagram.ProperMyDiagramElement;

public class PumlPackageExporter extends PumlElementExporter {

    public PumlPackageExporter(MyElementTypeRepository myElementTypeRepository) {
       super(myElementTypeRepository, MyElementTypePackage.class);
    }

    @Override
    public void to(PumlDiagram result, ProperMyDiagramElement myDiagramElement) {
        var myElement = myDiagramElement.getModelElement();
        result.beginBlock("namespace " + myElement.getName());
        for (var aSubShape : myDiagramElement.getSubShapes()) {
            var exporter = aSubShape.getModelElement().getElementType().getPumlElementExporter();
            exporter.to(result, aSubShape);
        }
        result.endBlock();

    }
}
