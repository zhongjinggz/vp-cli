package plugins.vpcli.models;

public class History extends StateData {

    private boolean deep;

    public History(String name) {
        super(name);
    }

    public boolean isDeep() {
        return deep;
    }

    public void setDeep(boolean deep) {
        this.deep = deep;
    }
}
