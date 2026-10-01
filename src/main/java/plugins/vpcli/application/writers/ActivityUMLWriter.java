package plugins.vpcli.application.writers;

import plugins.vpcli.domain.myuml.myactivity.MyAction;
import plugins.vpcli.domain.myuml.myactivity.MyFlowNode;
import plugins.vpcli.domain.myuml.myactivity.MyJoinFlowNode;
import plugins.vpcli.domain.myuml.myactivity.MySplitFlowNode;
import plugins.vpcli.domain.myuml.mycommon.MyNote;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class ActivityUMLWriter extends PlantUMLWriter {

    private final MyFlowNode rootFlowNode;
    private final Set<MyFlowNode> processedNodes = new HashSet<>();
    private final Stack<MyJoinFlowNode> joinStack = new Stack<>();
    private String activeSwimlane = "";

    public ActivityUMLWriter(List<MyNote> notes, MyFlowNode rootFlowNode) {
        super(notes);
        this.rootFlowNode = rootFlowNode;
    }

    @Override
    public void writeToFile(File file) throws IOException {
        StringBuilder plantUMLContent = new StringBuilder("@startuml\n");

        generateFlowUML(rootFlowNode, plantUMLContent);

        plantUMLContent.append("@enduml");

        try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8)) {
            writer.write(plantUMLContent.toString());
        }
    }

    private void generateFlowUML(MyFlowNode node, StringBuilder plantUMLContent) {
        if (node == null || processedNodes.contains(node)) {
            return;
        }

        processedNodes.add(node);

        if (node instanceof MyAction) {
            MyAction action = (MyAction) node;
            if (action.getSwimlane() != null) {
                if (!Objects.equals(activeSwimlane, action.getSwimlane()))
                    plantUMLContent.append("|").append(action.getSwimlane()).append("|\n");
                activeSwimlane = action.getSwimlane();
            }

            if (action.isInitial()) {
               plantUMLContent.append("start\n");
            } else if (action.isFinal()) {
                plantUMLContent.append("stop\n");
            } else if (action.isFinalFlow()) {
                plantUMLContent.append("end\n");
            } else {
                plantUMLContent.append(":").append(action.getName()).append(";\n");
            }

            if (action.getNextLabel() != null && !action.getNextLabel().isEmpty()) plantUMLContent.append("-> ").append(action.getNextLabel()).append(";\n");

            if (action.getNextNode() != null) {
                generateFlowUML(action.getNextNode(), plantUMLContent);
            } else if (!action.isFinal() && !action.isFinalFlow()) {
                plantUMLContent.append("kill\n");
            }

        }
        // Handle SplitFlowNode
        else if (node instanceof MySplitFlowNode) {
            MySplitFlowNode splitNode = (MySplitFlowNode) node;
            String type = splitNode.getType();

            if ("decision".equals(type)) {
                writeDecision(plantUMLContent, splitNode);
            } else if ("fork".equals(type)) {
                writeForkAndJoin(plantUMLContent, splitNode);
            }
        } else if (node instanceof MyJoinFlowNode) {
            MyJoinFlowNode joinNode = (MyJoinFlowNode) node;
            if (!joinStack.contains(joinNode)) joinStack.push(joinNode);
        }
    }

    private void writeDecision(StringBuilder plantUMLContent, MySplitFlowNode decisionNode) {


        if (decisionNode.getSwimlane() != null) {
            if (!Objects.equals(activeSwimlane, decisionNode.getSwimlane()))
                plantUMLContent.append("|").append(decisionNode.getSwimlane()).append("|\n");
            activeSwimlane = decisionNode.getSwimlane();
        }

        List<MyFlowNode> branches = decisionNode.getBranches();
        String branchLabel = branches.get(0).getPrevLabelBranch();
        boolean isSwitch = false;

        if (branches.size() > 2) {
            plantUMLContent.append("switch (").append(decisionNode.getName()).append(") \n");
            plantUMLContent.append("case ");

            if (branchLabel != null && !branchLabel.isEmpty()) {
                plantUMLContent.append("(").append(branchLabel).append(")");
            } else plantUMLContent.append("()");

            isSwitch = true;
        } else
            plantUMLContent.append("if (").append(decisionNode.getName()).append(") then ");




        if (branchLabel != null && !branchLabel.isEmpty() && !isSwitch)
            plantUMLContent.append("(").append(branchLabel).append(")");
        plantUMLContent.append("\n");
        for (int i = 0; i < branches.size(); i++) {
            generateFlowUML(branches.get(i), plantUMLContent);
            if (i < branches.size() - 1) {
                String branchLabel2 = branches.get(i+1).getPrevLabelBranch();
                plantUMLContent.append( isSwitch? "case " : "else ");
                if (branchLabel2 != null && !branchLabel2.isEmpty() && !isSwitch)
                     plantUMLContent.append("(").append(branches.get(i + 1).getPrevLabelBranch()).append(")");
                else if (isSwitch) {
                    plantUMLContent.append("()");
                }
                plantUMLContent.append("\n");
            }
        }
        if (isSwitch) plantUMLContent.append("endswitch\n");
        else plantUMLContent.append("endif\n");

        if (!joinStack.isEmpty()) {
            MyJoinFlowNode join = joinStack.pop();
            generateFlowUML(join.getNextNode(), plantUMLContent);
        }
        MyFlowNode continuationNode = findJoinContinuation(decisionNode);
        if (continuationNode != null) {
            generateFlowUML(continuationNode, plantUMLContent);
        }
    }

    private void writeForkAndJoin(StringBuilder plantUMLContent, MySplitFlowNode forkNode) {
        plantUMLContent.append("fork\n");

        List<MyFlowNode> branches = forkNode.getBranches();
        for (int i = 0; i < branches.size(); i++) {
            generateFlowUML(branches.get(i), plantUMLContent);
            if (i < branches.size() - 1) {
                plantUMLContent.append("fork again\n");
            }
        }

        if (!joinStack.isEmpty()) {
            MyJoinFlowNode join = joinStack.pop();
            if (join.isMerge()) plantUMLContent.append("end merge\n");
            else plantUMLContent.append("end fork\n");
            generateFlowUML(join.getNextNode(), plantUMLContent);
        }
        MyFlowNode continuationNode = findJoinContinuation(forkNode);
        if (continuationNode != null) {
            generateFlowUML(continuationNode, plantUMLContent);
        }
    }

    private MyFlowNode findJoinContinuation(MySplitFlowNode forkNode) {
        for (MyFlowNode branch : forkNode.getBranches()) {
            if (branch instanceof MyJoinFlowNode) {
                return ((MyJoinFlowNode) branch).getNextNode();
            }
        }
        return null;
    }
}
