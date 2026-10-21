package plugins.vpcli.domain.myuml_old.myinteraction_old;

import plugins.vpcli.domain.myuml_old.mycommon_old.MyElement_old;

import java.util.ArrayList;
import java.util.List;

public class MyLifeline extends MyElement_old {

	private List<String> stereotypes = new ArrayList<>();
	private boolean isCreatedByMessage;
	private String classifier;
	private String alias;

	public MyLifeline(String name) {
		super(name);
    }

	public List<String> getStereotypes() {
		return stereotypes;
	}

	public void setStereotypes(List<String> stereotypes) {
		this.stereotypes = stereotypes;
	}

	public boolean isCreatedByMessage() {
		return isCreatedByMessage;
	}

	public void setCreatedByMessage(boolean isCreatedByMessage) {
		this.isCreatedByMessage = isCreatedByMessage;
	}

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    public String getAlias() {
        return alias;
    }

}
