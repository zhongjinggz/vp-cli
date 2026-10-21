package plugins.vpcli.domain.myuml_old.myactivity_old;

import plugins.vpcli.domain.myuml_old.mycommon_old.MyElement_old;

public class MyJoinFlowNode extends MyElement_old implements MyFlowNode {

    private MyFlowNode nextNode;
    private String prevLabel;
    private boolean isMerge;

    public MyJoinFlowNode(String name) {
        super(name);
    }

    public MyFlowNode getNextNode() {
        return nextNode;
    }

    public void setNextNode(MyFlowNode nextNode) {
        this.nextNode = nextNode;
    }

    @Override
    public String getPrevLabelBranch() {
        return this.prevLabel;
    }

    @Override
    public void setPrevLabelBranch(String label) {
        this.prevLabel = label;
    }

    public boolean isMerge() {
        return isMerge;
    }

    public void setMerge(boolean merge) {
        isMerge = merge;
    }
}
