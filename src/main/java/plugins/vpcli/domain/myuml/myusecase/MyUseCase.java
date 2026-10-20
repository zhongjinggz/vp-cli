package plugins.vpcli.domain.myuml.myusecase;

import plugins.vpcli.domain.myuml.mycommon.ElementType;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.myelementtype.MyElementType;

import java.util.List;

public class MyUseCase extends MyElement {
	private List<String> stereotypes;
	private boolean isInPackage;
	private boolean isBusiness;
	private String Uid;


	public MyUseCase(MyElement parent, String id, String name
//		, MyElementType myElementType
		, String Description) {
        super(parent, id, name, ElementType.USE_CASE
//			, myElementType
			, Description);
	}
	public List<String> getStereotypes() {
		return stereotypes;
	}

	public void setStereotypes(List<String> stereotypes) {
		this.stereotypes = stereotypes;
	}


	public boolean isInPackage() {
		return isInPackage;
	}

	public void setInPackage(boolean isInPackage) {
		this.isInPackage = isInPackage;
	}
	public boolean isBusiness() {
		return isBusiness;
	}
	public void setBusiness(boolean isBusiness) {
		this.isBusiness = isBusiness;
	}

    public String getUid() {
		return  Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }
}
