package plugins.vpcli.domain;

public interface FlowNode {
    String getPrevLabelBranch();
    void setPrevLabelBranch(String label);
}
