package plugins.vpcli.domain.myuml.mynary;

import plugins.vpcli.domain.myuml.mycommon.MyElement;

public class MyNary extends MyElement {
    private final String name;
    private final String id;
    private final String alias;
    private String Uid;

    public MyNary(String name, String id) {
        super(name);
    	this.name = (name != null && !name.isEmpty()) ? name : null;
        this.id = id;
        this.alias = generateAlias();
    }
    
    private String generateAlias() {
       // if (this.name == null) {
    	if(this.id != null) 
    		return "diamond_" + id.replaceAll("[^a-zA-Z0-9]", "_");
        //} 
        // return name;
		return null;
    }
    
    public String getId() {
        return id;
    }

    public String getAlias() {
        return alias;
    }

	public String getUid() {
		return Uid;
	}

	public void setUid(String uid) {
		Uid = uid;
	}

	@Override
	public String getName() {
		if (name != null) {
			return name;
		} 
		// unnamed nary
		return " ";
	}
}
