package plugins.vpcli.domain.myuml.classifier;

import plugins.vpcli.domain.myuml.mystatemachine.BaseWithSemanticsData;
import plugins.vpcli.domain.myuml.deployment.ArtifactData;

import java.util.ArrayList;
import java.util.List;

public class ComponentData extends BaseWithSemanticsData {

	private final boolean isInPackage;
	private List<String> stereotypes;
	private String Uid;
	private final List<ComponentData> residents;
    private boolean isResident;
	private final List<PortData> ports;
	private boolean isNodeComponent; // components and nodes are basically the same.
	private final List<ArtifactData> artifacts;
	private List<AttributeData> attributes;
	private List<OperationData> operations;


	public ComponentData(String name, boolean isInPackage) {
		super(name);
		this.isInPackage = isInPackage;
		this.stereotypes = new ArrayList<>();
		this.residents = new ArrayList<>();
        this.ports = new ArrayList<>();
		this.artifacts = new ArrayList<>();
		this.setOperations(new ArrayList<>());
		this.setAttributes(new ArrayList<>());
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

	public List<ArtifactData> getArtifacts() {
		return artifacts;
	}

	public String getUid() {
		return Uid;
	}

	public void setUid(String Uid) {
		this.Uid = Uid;
	}

	public boolean isResident() {
		return isResident;
	}

	public void setResident(boolean isResident) {
		this.isResident = isResident;
	}

	public List<ComponentData> getResidents() {
		return residents;
	}

	public List<PortData> getPorts() {
		return ports;
	}

    public boolean isNodeComponent() {
        return isNodeComponent;
    }

    public void setNodeComponent(boolean nodeComponent) {
        isNodeComponent = nodeComponent;
    }

    public List<AttributeData> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeData> attributes) {
        this.attributes = attributes;
    }

    public List<OperationData> getOperations() {
        return operations;
    }

    public void setOperations(List<OperationData> operations) {
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
