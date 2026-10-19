package plugins.vpcli.domain.myuml.mycommon.myelementtype;

import plugins.vpcli.domain.outputmodel.PumlElementExporter;

public class MyElementTypeProject extends MyElementType {

    public MyElementTypeProject(
        PumlElementExporter pumlElementExporter
    ) {
        super("Project",
            "project_has_no_visual_paradigm_model_type",
            pumlElementExporter
        );
    }

    public MyElementTypeProject() {
        super("Project"
            , "project_has_no_visual_paradigm_model_type"
        );
    }
}
