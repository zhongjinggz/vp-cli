package plugins.vpcli.domain.myuml.ustatemachine;

import plugins.vpcli.domain.myuml.ucommon.SemanticsData;
import plugins.vpcli.domain.myuml.ucommon.UElement;

public class BaseWithSemanticsData extends UElement {
	
	private SemanticsData semantics;
	
	public BaseWithSemanticsData(String name) {
		this.setName(name);
		this.setSemantics(new SemanticsData());
	}

	public SemanticsData getSemantics() {
		return semantics;
	}

	public void setSemantics(SemanticsData semantics) {
		this.semantics = semantics;
	}
	
}
