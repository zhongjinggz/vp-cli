package plugins.vpcli.domain.myuml.uinteraction;

import java.util.ArrayList;
import java.util.List;

public class InteractionRef {
	private final List<String> coveredLifelines = new ArrayList<>();
	private String refName;
	
	public InteractionRef(String referenceName) {
		this.setRefName(referenceName);
	}

	public List<String> getCoveredLifelines() {
		return coveredLifelines;
	}

	public String getRefName() {
		return refName;
	}

	public void setRefName(String refName) {
		this.refName = refName;
	}
}
