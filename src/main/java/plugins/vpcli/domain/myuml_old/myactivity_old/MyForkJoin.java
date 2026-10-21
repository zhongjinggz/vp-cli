package plugins.vpcli.domain.myuml_old.myactivity_old;

import plugins.vpcli.domain.myuml_old.mystatemachine_old.MyState;

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
