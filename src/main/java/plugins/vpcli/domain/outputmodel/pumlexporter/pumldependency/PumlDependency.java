package plugins.vpcli.domain.outputmodel.pumlexporter.pumldependency;

import java.util.ArrayList;
import java.util.List;

public class PumlDependency {
    private final String fromAlias;
    private final String toAlias;
    private final List<String> labels = new ArrayList<>();

    public PumlDependency(String fromAlias, String toAlias, List<String> labels) {
        this.fromAlias = fromAlias;
        this.toAlias = toAlias;
        this.labels.addAll(labels);
    }

    static PumlDependency of(
        String fromAlias,
        String toAlias,
        List<String> getLabels
    ) {
        return new PumlDependency(
            fromAlias,
            toAlias,
            getLabels
        );
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(fromAlias);
        result.append(" ..> ");
        result.append(toAlias);
        if (!labels.isEmpty()) {
            result.append(": ");
        }
        for (String label : labels) {
            result.append("<<");
            result.append(label);
            result.append(">>");
        }
        return result.toString();
    }
}
