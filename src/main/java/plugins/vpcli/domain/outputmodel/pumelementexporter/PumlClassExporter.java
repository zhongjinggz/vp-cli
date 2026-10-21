package plugins.vpcli.domain.outputmodel.pumelementexporter;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class PumlClassExporter extends PumlBlockExporter {
    @Inject
    public PumlClassExporter() {
    }

    @Override
    String getBlockType() {
        return "class";
    }
}
