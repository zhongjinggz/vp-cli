package plugins.vpcli.domain.myuml.mycommon.myelementtype;

import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;
import plugins.vpcli.domain.outputmodel.PumlElementExporter;

public class MyElementType {
    private final PumlElementExporter pumlElementExporter;
    private final MyElementFactory myElementFactory;
    private String name;
    private String vpModelType;

    public MyElementType(
        String name,
        String vpModelType,
        PumlElementExporter pumlElementExporter,
        MyElementFactory myElementFactory
    ) {
        this.name = name;
        this.vpModelType = vpModelType;
        this.pumlElementExporter = pumlElementExporter;
        this.myElementFactory = myElementFactory;
    }

    public PumlElementExporter getPumlElementExporter() {
        return pumlElementExporter;
    }

    public MyElementFactory getMyElementFactory() {
        return myElementFactory;
    }
}
