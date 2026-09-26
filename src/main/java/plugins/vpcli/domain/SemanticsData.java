package plugins.vpcli.domain;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY) // Annotation to omit empty or null fields when constructing the JSON
public class SemanticsData {

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<Reference> references = new ArrayList<>();
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<SubDiagramData> subDiagrams = new ArrayList<>();
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String description;
	private String ownerName ;
	private String ownerType;

	public void setReferences(List<Reference> references) {
		this.references = references;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setSubDiagrams(List<SubDiagramData> subDiagrams) {
		this.subDiagrams = subDiagrams;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public void setOwnerType(String ownerType) {
		this.ownerType = ownerType;
	}
}
