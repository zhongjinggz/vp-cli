package plugins.vpcli.domain;

import java.util.ArrayList;
import java.util.List;

public class SplitFlowNode extends BaseWithSemanticsData implements FlowNode {

    private final String type;
    private final List<FlowNode> branches = new ArrayList<>();
    private String prevLabel;
    private String swimlane;

    public SplitFlowNode(String name, String type) {
        super(name);
        this.type = type;
    }

    public List<FlowNode> getBranches() {
        return branches;
    }

    public String getType() {
        return type;
    }

    public void addBranch(FlowNode node) {
        branches.add(node);
    }

    @Override
    public String getPrevLabelBranch() {
        return this.prevLabel;
    }

    @Override
    public void setPrevLabelBranch(String label) {
        this.prevLabel = label;
    }

    public void setMergeStyleJoin(boolean mergeStyleJoin) {
    }

    public String getSwimlane() {
        return swimlane;
    }

    public void setSwimlane(String swimlane) {
        this.swimlane = swimlane;
    }
}
