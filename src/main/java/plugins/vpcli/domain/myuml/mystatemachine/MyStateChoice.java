package plugins.vpcli.domain.myuml.mystatemachine;

public class MyStateChoice extends MyState {
    private String Uid;

    public MyStateChoice(String name) {
        super(name);
    }


    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }
}
