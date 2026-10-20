package plugins.vpcli.domain.myuml.myassociation;

import plugins.vpcli.domain.myuml.mycommon.ElementType;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.myelementtype.MyElementType;

import java.util.List;

public class MyAssociation extends MyElement {

    public MyAssociation(
        MyElement parent,
        String id,
		String name,
//		MyElementType myElementType,
		String Description
	) {
        super(parent, id, name, ElementType.ASSOCIATION
//			, myElementType
			, Description);
    }









}
