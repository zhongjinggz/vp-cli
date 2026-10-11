package plugins.vpcli.domain.myuml.myusecase;

import plugins.vpcli.domain.myuml.mycommon.MyElement;

import java.util.List;

public class MyActor extends MyElement {
	private boolean isBusiness;

	public MyActor(String name) {
		super(name);
	}


	public boolean isBusiness() {
		return isBusiness;
	}

	public void setBusiness(boolean isBusiness) {
		this.isBusiness = isBusiness;
	}

}
