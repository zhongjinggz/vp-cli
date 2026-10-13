package plugins.vpcli.domain.outputmodel;

import plugins.vpcli.domain.myuml.mycommon.ElementType;

public class PumlElementExporterFactory {
    public PumlElementExporter getExporter(ElementType elementType) {
        switch (elementType) {
            case PACKAGE:
                return new PumlPackageExporter();
            case CLASS:
                return new PumlClassExporter();
            case MODEL:
                return new PumlModelExporter();
            case USECASE:
                return new PumlUseCaseExporter();
            default:
                throw new IllegalArgumentException("Unknown elementType " + elementType);
        }
    }
}
