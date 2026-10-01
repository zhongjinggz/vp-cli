package plugins.vpcli.domain.myuml.mystatemachine;

public class MyHistory extends MyState {

    private boolean deep;

    public MyHistory(String name) {
        super(name);
    }

    public boolean isDeep() {
        return deep;
    }

    public void setDeep(boolean deep) {
        this.deep = deep;
    }
}
