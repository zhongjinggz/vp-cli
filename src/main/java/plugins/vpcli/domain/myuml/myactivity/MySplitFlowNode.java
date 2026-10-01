package plugins.vpcli.domain.myuml.myactivity;

import plugins.vpcli.domain.myuml.mycommon.MyElement;

import java.util.ArrayList;
import java.util.List;

public class MySplitFlowNode extends MyElement implements MyFlowNode {

    private final String type;
    private final List<MyFlowNode> branches = new ArrayList<>();
    private String prevLabel;
    private String swimlane;

    public MySplitFlowNode(String name, String type) {
        super(name);
        this.type = type;
    }

    public List<MyFlowNode> getBranches() {
        return branches;
    }

    public String getType() {
        return type;
    }

    public void addBranch(MyFlowNode node) {
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

    public String getSwimlane() {
        return swimlane;
    }

    public void setSwimlane(String swimlane) {
        this.swimlane = swimlane;
    }
}
