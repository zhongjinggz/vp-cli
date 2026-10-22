package plugins.vpcli.domain.outputmodel.pumelementexporter;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class PumlModelExporter extends PumlPackageExporter {
    @Inject
    public PumlModelExporter() {
    }

    @Override
    String getBlockType() {
        return "namespace";
    }

    @Override
    String getBlockSuffix() {
        return " <<model>>";
    }
}
