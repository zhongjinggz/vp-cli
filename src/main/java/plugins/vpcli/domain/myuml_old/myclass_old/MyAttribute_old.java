package plugins.vpcli.domain.myuml_old.myclass_old;

import java.util.Objects;

public class MyAttribute_old {
    private String name;
    private final String visibility;
    private final String type; // int, bool, etc.
    private String initialValue;
    private boolean isStatic;

    public MyAttribute_old(String visibility, String name, String type, String initValue, String scope) {
        this.setName(name);
        this.visibility = visibility;
        this.isStatic = (Objects.equals(scope, "classifier"));
        
        if (!Objects.equals(type, "")) {
            this.type = type;
            if (!Objects.equals(initValue, "")) {
                this.setInitialValue(initValue);
            }
        } else {
            this.type = null;
            this.setInitialValue(null);
        }
    }

    public String getVisibility() {
        return visibility;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(String initialValue) {
        this.initialValue = initialValue;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }
}
