package plugins.vpcli.application.writers;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import plugins.vpcli.domain.myuml.uclassifier.UComponent;
import plugins.vpcli.domain.myuml.uclassifier.UComponent.PortData;
import plugins.vpcli.domain.myuml.uclassifier.AttributeData;
import plugins.vpcli.domain.myuml.uclassifier.UClass;
import plugins.vpcli.domain.myuml.uclassifier.OperationData;
import plugins.vpcli.domain.myuml.ucommon.NoteData;
import plugins.vpcli.domain.myuml.upackage.UPackage;
import plugins.vpcli.domain.myuml.ucommon.RelationshipData;
import plugins.vpcli.domain.myuml.udeployment.UArtifact;

public class ComponentDeploymentUMLWriter extends PlantUMLWriter {

	private final List<UComponent> components;
	private final List<UClass> interfaces;
	private final List<UPackage> packages;
	private final List<RelationshipData> relationships;
	private final List<UArtifact> artifacts;

    public ComponentDeploymentUMLWriter(List<NoteData> notes, List<UComponent> components, List<UClass> interfaces, List<UArtifact> artifacts, List<UPackage> packages, List<RelationshipData> relationships) {
		super(notes);
		this.components = components;
		this.interfaces = interfaces;
		this.artifacts = artifacts;
		this.packages = packages;
		this.relationships = relationships;
	}

	@Override
	public void writeToFile(File file) throws IOException {
		StringBuilder plantUMLContent = new StringBuilder("@startuml\n");

		// Allowmixing parameter to allow for class-type interfaces
		plantUMLContent.append("allowmixing\n");

		for (UComponent uComponent : components) {
			if(!uComponent.isInPackage() && !uComponent.isResident())
				plantUMLContent.append(writeComponent(uComponent, ""));
		}

		for (UClass interfaceData : interfaces) {
			if(!interfaceData.isInPackage())  
				plantUMLContent.append(writeInterface(interfaceData, ""));
		}

		for (UArtifact uArtifact : artifacts) {
			if(!uArtifact.isInPackage() && !uArtifact.isInNode())
				plantUMLContent.append(writeArtifact(uArtifact, ""));
		}

		for (UPackage uPackage : packages) {
			if(!uPackage.isSubpackage())
				plantUMLContent.append(writePackage(uPackage, ""));
		}

		plantUMLContent.append(writeNotes());

		for (RelationshipData relationship : relationships) {
			plantUMLContent.append(writeRelationship(relationship));
		}

		plantUMLContent.append("@enduml");
		try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8)) {
			writer.write(plantUMLContent.toString());
		}
	}

	private String writeArtifact(UArtifact uArtifact, String indent) {
		StringBuilder artifactString = new StringBuilder();
		String name = formatName(uArtifact.getName());

		artifactString.append(indent).append("artifact ").append(name).append(" as ").append(formatAlias(uArtifact.getName())).append("\n");
		return artifactString.toString();
	}

	private String writeRelationship(RelationshipData relationship) {
		
		return relationship.toExportFormat();
    }
	
	private String writePackage(UPackage uPackage, String indent) {
		StringBuilder packageString = new StringBuilder();
		String name = formatName(uPackage.getName());

		packageString.append(indent).append("package " ).append(name).append(" {\n");

		for (UClass interfaceData : uPackage.getClasses()) {
			packageString.append(writeInterface(interfaceData, indent + "\t"));
		}

		for (UArtifact uArtifact : uPackage.getArtifacts()) {
			packageString.append(writeArtifact(uArtifact, indent + "\t"));
		}
		for (UComponent uComponent : uPackage.getComponents()) {
			packageString.append(writeComponent(uComponent, indent + "\t"));
		}

		for (UPackage subPackage : uPackage.getSubPackages()) {
			packageString.append(writePackage(subPackage, indent + "\t"));
		}

		packageString.append(indent).append("}\n");
		return packageString.toString();
	}

	private String writeInterface(UClass interfaceData, String indent) {
		StringBuilder interfaceString = new StringBuilder();
		String name = formatName(interfaceData.getName());
		String warningComment = "";

		String aliasDeclaration = formatAlias(interfaceData.getName()).equals(interfaceData.getName()) ? "" : (" as " + formatAlias(interfaceData.getName()));

		if (interfaceData.getName().isEmpty()) {
			warningComment = " ' You might need to name this interface \n";
			name = " \" \" ";

			aliasDeclaration = " as " + formatAlias(interfaceData.getUid());
		}
		interfaceString.append(warningComment);
		interfaceString.append(indent);
		boolean hasAttrAndOps = (!interfaceData.getAttributes().isEmpty() || !interfaceData.getOperations().isEmpty());
		List<String> filteredStereotypes;
		if (hasAttrAndOps) {
			interfaceString.append("class ");
			filteredStereotypes = interfaceData.getStereotypes();
		} else {
			interfaceString.append("() ");
			filteredStereotypes = interfaceData.getStereotypes().stream()
					.filter(stereotype -> !"interface".equalsIgnoreCase(stereotype)) // Skip "interface"
					.collect(Collectors.toList());
		}


		interfaceString.append(name).append(aliasDeclaration);

		if (!filteredStereotypes.isEmpty()) {
			String stereotypesString = filteredStereotypes.stream()
					.map(stereotype -> "<<" + stereotype + ">>")
					.collect(Collectors.joining(", "));
			interfaceString.append(" ").append(stereotypesString);
		}
		if (hasAttrAndOps) {
			interfaceString.append(" {\n");
			writeAttributesAndOperations(interfaceData.getAttributes(), interfaceData.getOperations(), indent, interfaceString);
			interfaceString.append(indent).append("}\n");
		}
		interfaceString.append("\n");
		return interfaceString.toString();
	}


	private String writeComponent(UComponent uComponent, String indent) {
		StringBuilder componentString = new StringBuilder();
		String name = formatName(uComponent.getName()) ;
		String aliasDeclaration = formatAlias(uComponent.getName()).equals(uComponent.getName()) ? "" : (" as " + formatAlias(uComponent.getName()));

		List<AttributeData> attributeData = uComponent.getAttributes();
		List<OperationData> operationData = uComponent.getOperations();
		boolean hasOpsAndAttrs = !attributeData.isEmpty() || !operationData.isEmpty();
		if (hasOpsAndAttrs) {
			// we will need to create a package to contain the component and the interface it will implement
			componentString.append("package ").append(name).append(" {\n");
		}
		componentString.append(indent);

		if (uComponent.isNodeComponent()) {
			componentString.append("node ");
		} else {
			componentString.append("component ");
		}
		componentString.append(name).append(aliasDeclaration);

		if (!uComponent.getStereotypes().isEmpty()) {
			String stereotypesString = uComponent.getStereotypes().stream()
					.filter(stereotype -> !"component".equals(stereotype)) // Exclude "component", VP auto applies it to every use case for some reason
					.map(stereotype -> "<<" + stereotype + ">>")
					.collect(Collectors.joining(", "));
			if (!stereotypesString.isEmpty()) { 
				componentString.append(" ").append(stereotypesString);
			}
		}
		// resident components & ports
		List<UComponent> residents = uComponent.getResidents();
		List<PortData> ports = uComponent.getPorts();
		List<UArtifact> artifacts = uComponent.getArtifacts();

		componentString.append(" {\n");

		if (residents != null && !residents.isEmpty()) {
			for (UComponent resident : residents) {
				componentString.append(writeComponent(resident, indent + "\t"));
			}
		}

		if (artifacts != null && !artifacts.isEmpty()) {
			for (UArtifact artifact : artifacts) {
				componentString.append(writeArtifact(artifact, indent + "\t"));
			}
		}
		
		if (ports != null && !ports.isEmpty()) {
			for (PortData port : ports) {
				
				String alias = port.getAlias();
				String portName = port.getName();
				
				componentString.append(indent).append("\t");
				componentString.append("port ")
                        .append("\"").append(portName).append("\"").append(" as ").append(alias).append("\n");
			}
		}
		
		componentString.append(indent).append("}");
		componentString.append("\n");

		if (hasOpsAndAttrs) {
			componentString.append(writeImplInterface(uComponent, indent));
			componentString.append("}\n");
		}
		return componentString.toString();
	}

	private String writeImplInterface(UComponent uComponent, String indent) {
		String name = formatName("I" + uComponent.getName());
		StringBuilder interfaceString = new StringBuilder();
		String aliasDeclaration = formatAlias(uComponent.getName()).equals(uComponent.getName()) ? "" : (" as I" + formatAlias(uComponent.getName()));

		interfaceString.append(indent).append("interface ").append(name).append(aliasDeclaration);
		interfaceString.append(" {\n");

		writeAttributesAndOperations(uComponent.getAttributes(), uComponent.getOperations(), indent, interfaceString);
		interfaceString.append(indent).append("}\n");

		interfaceString.append(indent).append(formatAlias(uComponent.getName())).append(" ..|> ").append("I").append(formatAlias(uComponent.getName())).append(" : implements");
		interfaceString.append("\n");
		return interfaceString.toString();
	}
}
