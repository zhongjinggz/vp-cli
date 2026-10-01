package plugins.vpcli.domain.myuml.mystatemachine;

import plugins.vpcli.domain.myuml.mycommon.MyRelationship;
import plugins.vpcli.domain.myuml.mycommon.MyElement;

import java.util.ArrayList;
import java.util.List;

public class MyState extends MyElement {

    private String Uid;
    private boolean isStart;
    private boolean isEnd;
    private final List<StateRegion> regions = new ArrayList<>();
    private boolean isInState;
    private String id;
    private String alias;


    public MyState(String name) {
        super(name);
    }

    private String generateAlias() {
        // if (this.name == null) {
        if(this.id != null)
            return "state_" + id.replaceAll("[^a-zA-Z0-9]", "_");
        //}
        // return name;
        return null;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public List<StateRegion> getRegions() {
        return regions;
    }

    public boolean isInState() {
        return isInState;
    }

    public void setInState(boolean inState) {
        isInState = inState;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        this.alias = generateAlias();
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }


    public static class StateRegion {
        private List<MyState> subStates = new ArrayList<>();
        private final List<MyRelationship> regTransitions = new ArrayList<>();

        public StateRegion() {

        }

        public List<MyState> getSubStates() {
            return subStates;
        }

        public List<MyRelationship> getRegTransitions() {
            return regTransitions;
        }

    }
}
