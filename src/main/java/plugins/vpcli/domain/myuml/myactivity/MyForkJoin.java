package plugins.vpcli.domain.myuml.myactivity;

import plugins.vpcli.domain.myuml.mystatemachine.MyState;

public class MyForkJoin extends MyState {

    private boolean isFork; // else Join

    public MyForkJoin(String name) {
        super(name);
    }

    public boolean isFork() {
        return isFork;
    }

    public void setFork(boolean fork) {
        isFork = fork;
    }
}
