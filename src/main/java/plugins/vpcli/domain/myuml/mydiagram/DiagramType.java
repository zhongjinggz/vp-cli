package plugins.vpcli.domain.myuml.mydiagram;

import java.util.Objects;

public enum DiagramType {
    CLASS("ClassDiagram", ".class-diagram"),
    COMPONENT("ComponentDiagram", ".component-diagram"),
    DEPLOYMENT("DeploymentDiagram", ".deployment-diagram"),
    INTERACTION("InteractionDiagram", ".interaction-diagram"),
    USECASE("UseCaseDiagram", "usecase-diagram"),
    STATE("StateDiagram", "state-diagram"),
    ACTIVITY("ActivityDiagram", ".activity-diagram");

    private final String vpDiagramType;
    private final String suffix;

    DiagramType(String vpDiagramType, String suffix) {
        this.vpDiagramType = vpDiagramType;
        this.suffix = suffix;
    }

    public static DiagramType of(String vpDiagramType) {
        for (var aValue : values()) {
            if (aValue.vpDiagramTypeIs(vpDiagramType)) {
                return aValue;
            }
        }
        return null;
    }

    public String getVpDiagramType() {
        return vpDiagramType;
    }

    public boolean vpDiagramTypeIs(String theType) {
        return Objects.equals(this.vpDiagramType, theType);
    }

    public String getSuffix() {
        return suffix;
    }
}
