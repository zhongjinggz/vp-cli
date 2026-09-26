package plugins.vpcli.domain;

import java.util.Objects;

public class AssociationData extends RelationshipData {

	private final String fromEndMultiplicity;
	private final String toEndMultiplicity;
	
	private String Uid;
	
	public AssociationData(String source, String target, String type, String name, String fromEndMultiplicity,
                           String toEndMultiplicity, String fromEndAggregation) {
		super(source, target, type, name);
		this.fromEndMultiplicity = fromEndMultiplicity == null ? "" : fromEndMultiplicity;
		this.toEndMultiplicity = toEndMultiplicity == null ? "" : toEndMultiplicity;
		if (Objects.equals(fromEndAggregation, "shared")) {
			this.setType("Aggregation");
		} else if (Objects.equals(fromEndAggregation, "composite")) {
			this.setType("Composition");
		} else {
			this.setType("Simple");
		}
	}

	@Override
	public String toExportFormat() {
	    StringBuilder output = new StringBuilder();

	    // Define symbol and navigability markers
	    String symbol = "--";
	    String prefix = (!getName().isEmpty()) ? " : " : "";
	    String fromMultiplicity = (fromEndMultiplicity != null && !fromEndMultiplicity.isEmpty()) ? "\"" + fromEndMultiplicity + "\" " : "";
	    String toMultiplicity = (toEndMultiplicity != null && !toEndMultiplicity.isEmpty()) ? " \"" + toEndMultiplicity + "\"" : "";
	    
	    // Determine the association type symbol
	    String type = getType();
	    if ("Aggregation".equals(type)) {
	        symbol = "o--";
	    } else if ("Composition".equals(type)) {
	        symbol = "*--";
	    }

	    // Construct the export format string with multiplicities and navigability
	    output.append(formatAlias(getSource()))
	          .append(" ")
	          .append(fromMultiplicity)
	          .append(symbol)
	          .append(toMultiplicity)
	          .append(" ")
	          .append(formatAlias(getTarget()))
	          .append(prefix)
	          .append(getName())
	          .append("\n");

	    return output.toString();
	}


	public String getUid() {
		return Uid;
	}

	public void setUid(String uid) {
		Uid = uid;
	}

}
	
