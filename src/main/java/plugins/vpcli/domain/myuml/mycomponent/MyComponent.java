package plugins.vpcli.domain.myuml.mycomponent;

import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml_old.myclass_old.MyAttribute_old;
import plugins.vpcli.domain.myuml_old.myclass_old.MyOperation_old;
import plugins.vpcli.domain.myuml_old.mydeployment_old.MyArtifact_old;

import java.util.ArrayList;
import java.util.List;

public class MyComponent extends MyElement {

	private final boolean isInPackage;
	private String Uid;
	private final List<MyComponent> residents;
	private final List<PortData> ports;
	private final List<MyArtifact_old> artifacts;
	private List<MyAttribute_old> attributes;
	private List<MyOperation_old> operations;


	public MyComponent(String name, boolean isInPackage) {
		super(name);
		this.isInPackage = isInPackage;
		this.residents = new ArrayList<>();
        this.ports = new ArrayList<>();
		this.artifacts = new ArrayList<>();
		this.setOperations(new ArrayList<>());
		this.setAttributes(new ArrayList<>());
	}


	public boolean isInPackage() {
		return isInPackage;
	}

	public String getUid() {
		return Uid;
	}

	public void setUid(String Uid) {
		this.Uid = Uid;
	}

	public void setAttributes(List<MyAttribute_old> attributes) {
        this.attributes = attributes;
    }

	public void setOperations(List<MyOperation_old> operations) {
        this.operations = operations;
    }

    public static class PortData {
		private final String name;
		private String id;
		private String alias;
		private String Uid;

		public PortData(String name) {
			this.name = (name != null && !name.isEmpty()) ? name : null;
			
		}

		private String generateAlias() {
			//if (this.name == null) {
			return "port_" + id.replaceAll("[^a-zA-Z0-9]", "_");
			//} 
			// return name;
		}
		
		public void setId(String id) {
			this.id = id;
			this.alias = generateAlias();
		}

		public String getId() {
			return id;
		}

		public String getAlias() {
			return alias;
		}

		public String getName() {
			if (name != null) {
				return name;
			} 
			// unnamed 
			return " ";
		}


		public String getUid() {
			return Uid;
		}

		public void setUid(String uid) {
			Uid = uid;
		}
	}

}
