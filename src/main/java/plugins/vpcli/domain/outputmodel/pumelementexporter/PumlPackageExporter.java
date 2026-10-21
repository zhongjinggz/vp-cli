package plugins.vpcli.domain.outputmodel.pumelementexporter;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class PumlPackageExporter extends PumlBlockExporter {

    @Inject
    public PumlPackageExporter() {
    }

    @Override
    String getBlockType() {
        return "namespace";
    }
}
