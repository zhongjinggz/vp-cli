package plugins.vpcli.application.writers;

import plugins.vpcli.domain.myuml.uactivity.ForkJoin;
import plugins.vpcli.domain.myuml.ucommon.NoteData;
import plugins.vpcli.domain.myuml.ucommon.RelationshipData;
import plugins.vpcli.domain.myuml.ustatemachine.History;
import plugins.vpcli.domain.myuml.ustatemachine.StateChoice;
import plugins.vpcli.domain.myuml.ustatemachine.StateData;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class StateUMLWriter extends PlantUMLWriter {

    private final List<StateData> states;
    private final List<RelationshipData> transitions;
    private final List<StateChoice> choices;
    private final List<History> histories;
    private final List<ForkJoin> forkJoins;

    public StateUMLWriter(List<NoteData> notes, List<StateData> states, List<RelationshipData> transitions, List<StateChoice> choices, List<History> histories, List<ForkJoin> forkJoins) {
        super(notes);
        this.states = states;
        this.choices = choices;
        this.histories = histories;
        this.transitions = transitions;
        this.forkJoins = forkJoins;
    }

    @Override
    public void writeToFile(File file) throws IOException {

        StringBuilder plantUMLContent = new StringBuilder("@startuml\n");

        for (StateData stateData : states) {
            if(!stateData.isInState())
                plantUMLContent.append(writeState(stateData, ""));
        }

        for (StateChoice stateChoice : choices) {
            if(!stateChoice.isInState()) {
                plantUMLContent.append(writeChoice(stateChoice, ""));
            }
        }

        for (History history : histories) {
            if (!history.isInState()) {
                plantUMLContent.append(writeHistory(history, ""));
            }
        }

        for (ForkJoin forkJoin : forkJoins) {
            if (!forkJoin.isInState()) {
                plantUMLContent.append(writeState(forkJoin, ""));
            }
        }

        plantUMLContent.append(writeNotes());

        for (RelationshipData transition : transitions) {
            plantUMLContent.append(writeRelationship(transition));
        }

        plantUMLContent.append("@enduml");
        try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8)) {
            writer.write(plantUMLContent.toString());
        }
    }

    private String writeHistory(History history, String indent) {
        StringBuilder historyString = new StringBuilder();
        String stereo = (history.isDeep() ? "<<history*>>" : "<<history>>");
        historyString.append(indent).append("state ").append(history.getAlias()).append( " ").append(stereo).append("\n");

        return historyString.toString();
    }

    private String writeChoice(StateChoice stateChoice, String indent) {

        return indent + "state " + stateChoice.getAlias() + " <<choice>>\n";
    }

    private String writeRelationship(RelationshipData transition) {
        return transition.toExportFormat();
    }

    private String writeState(StateData stateData, String indent) {
        StringBuilder stateString = new StringBuilder();
        boolean isStart = stateData.isStart();
        boolean isEnd = stateData.isEnd();
        String alias = stateData.getAlias();

        String name = stateData.getName();
        stateString.append(indent).append("state \" \" as ").append(alias);

        if (isStart) {
            return stateString.append(" <<start>>\n").toString();
        }

        if (isEnd) {
            return stateString.append(" <<end>>\n").toString();
        }

        if (stateData instanceof ForkJoin) {
            stateString.append(((ForkJoin) stateData).isFork() ? " <<fork>>\n" : " <<join>>\n");
            return stateString.toString();
        }


        if (name != null) {
            // Escape newlines
            name = name.replace("\n", "\\n");
        }

        stateString.append(writeRegions(stateData, indent));
        stateString.append(" : ").append(name);
        stateString.append("\n");
        return  stateString.toString();
    }

    private String writeRegions(StateData stateData, String indent) {
        StringBuilder stateString = new StringBuilder();
        if (!stateData.getRegions().isEmpty()) {
            stateString.append( " {\n");

            List<StateData.StateRegion> regions = stateData.getRegions();
            for (int i = 0; i < regions.size(); i++) {
                StateData.StateRegion region = regions.get(i);
                for (StateData stateInRegion : region.getSubStates()) {
                    if (stateInRegion instanceof History) {
                        stateString.append(writeHistory((History) stateInRegion, indent + "\t"));
                    } else {
                        stateString.append(writeState(stateInRegion, indent + "\t"));
                    }
                }

                for (RelationshipData transition : region.getRegTransitions()) {
                    stateString.append(indent).append("\t").append(transition.toExportFormat());
                }
                // concurrent if there's more than one region
                if (i < regions.size() - 1) {
                    stateString.append(indent).append("\t").append("--\n");
                }
            }

            stateString.append("}\n");
            stateString.append(indent).append("state ").append(stateData.getAlias());
        }
        return stateString.toString();
    }
}
