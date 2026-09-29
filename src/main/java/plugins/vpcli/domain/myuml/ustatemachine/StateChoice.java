package plugins.vpcli.domain.myuml.ustatemachine;

public class StateChoice extends StateData {
    private String Uid;

    public StateChoice(String name) {
        super(name);
    }


    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }
}
