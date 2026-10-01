package plugins.vpcli.application.writers;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import plugins.vpcli.domain.myuml.myclassifier.MyComponent;
import plugins.vpcli.domain.myuml.myclassifier.MyComponent.PortData;
import plugins.vpcli.domain.myuml.myclassifier.AttributeData;
import plugins.vpcli.domain.myuml.myclassifier.MyClass;
import plugins.vpcli.domain.myuml.myclassifier.MyOperation;
import plugins.vpcli.domain.myuml.mycommon.MyNote;
import plugins.vpcli.domain.myuml.mypackage.MyPackage;
import plugins.vpcli.domain.myuml.mycommon.MyRelationship;
import plugins.vpcli.domain.myuml.mydeployment.MyArtifact;

public class ComponentDeploymentUMLWriter extends PlantUMLWriter {

	private final List<MyComponent> components;
	private final List<MyClass> interfaces;
	private final List<MyPackage> packages;
	private final List<MyRelationship> relationships;
	private final List<MyArtifact> artifacts;

    public ComponentDeploymentUMLWriter(List<MyNote> notes, List<MyComponent> components, List<MyClass> interfaces, List<MyArtifact> artifacts, List<MyPackage> packages, List<MyRelationship> relationships) {
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

		for (MyComponent myComponent : components) {
			if(!myComponent.isInPackage() && !myComponent.isResident())
				plantUMLContent.append(writeComponent(myComponent, ""));
		}

		for (MyClass interfaceData : interfaces) {
			if(!interfaceData.isInPackage())  
				plantUMLContent.append(writeInterface(interfaceData, ""));
		}

		for (MyArtifact uArtifact : artifacts) {
			if(!uArtifact.isInPackage() && !uArtifact.isInNode())
				plantUMLContent.append(writeArtifact(uArtifact, ""));
		}

		for (MyPackage uPackage : packages) {
			if(!uPackage.isSubpackage())
				plantUMLContent.append(writePackage(uPackage, ""));
		}

		plantUMLContent.append(writeNotes());

		for (MyRelationship relationship : relationships) {
			plantUMLContent.append(writeRelationship(relationship));
		}

		plantUMLContent.append("@enduml");
		try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8)) {
			writer.write(plantUMLContent.toString());
		}
	}

	private String writeArtifact(MyArtifact uArtifact, String indent) {
		StringBuilder artifactString = new StringBuilder();
		String name = formatName(uArtifact.getName());

		artifactString.append(indent).append("artifact ").append(name).append(" as ").append(formatAlias(uArtifact.getName())).append("\n");
		return artifactString.toString();
	}

	private String writeRelationship(MyRelationship relationship) {
		
		return relationship.toExportFormat();
    }
	
	private String writePackage(MyPackage uPackage, String indent) {
		StringBuilder packageString = new StringBuilder();
		String name = formatName(uPackage.getName());

		packageString.append(indent).append("package " ).append(name).append(" {\n");

		for (MyClass interfaceData : uPackage.getClasses()) {
			packageString.append(writeInterface(interfaceData, indent + "\t"));
		}

		for (MyArtifact uArtifact : uPackage.getArtifacts()) {
			packageString.append(writeArtifact(uArtifact, indent + "\t"));
		}
		for (MyComponent myComponent : uPackage.getComponents()) {
			packageString.append(writeComponent(myComponent, indent + "\t"));
		}

		for (MyPackage subPackage : uPackage.getSubPackages()) {
			packageString.append(writePackage(subPackage, indent + "\t"));
		}

		packageString.append(indent).append("}\n");
		return packageString.toString();
	}

	private String writeInterface(MyClass interfaceData, String indent) {
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


	private String writeComponent(MyComponent myComponent, String indent) {
		StringBuilder componentString = new StringBuilder();
		String name = formatName(myComponent.getName()) ;
		String aliasDeclaration = formatAlias(myComponent.getName()).equals(myComponent.getName()) ? "" : (" as " + formatAlias(myComponent.getName()));

		List<AttributeData> attributeData = myComponent.getAttributes();
		List<MyOperation> operationData = myComponent.getOperations();
		boolean hasOpsAndAttrs = !attributeData.isEmpty() || !operationData.isEmpty();
		if (hasOpsAndAttrs) {
			// we will need to create a package to contain the component and the interface it will implement
			componentString.append("package ").append(name).append(" {\n");
		}
		componentString.append(indent);

		if (myComponent.isNodeComponent()) {
			componentString.append("node ");
		} else {
			componentString.append("component ");
		}
		componentString.append(name).append(aliasDeclaration);

		if (!myComponent.getStereotypes().isEmpty()) {
			String stereotypesString = myComponent.getStereotypes().stream()
					.filter(stereotype -> !"component".equals(stereotype)) // Exclude "component", VP auto applies it to every use case for some reason
					.map(stereotype -> "<<" + stereotype + ">>")
					.collect(Collectors.joining(", "));
			if (!stereotypesString.isEmpty()) { 
				componentString.append(" ").append(stereotypesString);
			}
		}
		// resident components & ports
		List<MyComponent> residents = myComponent.getResidents();
		List<PortData> ports = myComponent.getPorts();
		List<MyArtifact> artifacts = myComponent.getArtifacts();

		componentString.append(" {\n");

		if (residents != null && !residents.isEmpty()) {
			for (MyComponent resident : residents) {
				componentString.append(writeComponent(resident, indent + "\t"));
			}
		}

		if (artifacts != null && !artifacts.isEmpty()) {
			for (MyArtifact artifact : artifacts) {
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
			componentString.append(writeImplInterface(myComponent, indent));
			componentString.append("}\n");
		}
		return componentString.toString();
	}

	private String writeImplInterface(MyComponent myComponent, String indent) {
		String name = formatName("I" + myComponent.getName());
		StringBuilder interfaceString = new StringBuilder();
		String aliasDeclaration = formatAlias(myComponent.getName()).equals(myComponent.getName()) ? "" : (" as I" + formatAlias(myComponent.getName()));

		interfaceString.append(indent).append("interface ").append(name).append(aliasDeclaration);
		interfaceString.append(" {\n");

		writeAttributesAndOperations(myComponent.getAttributes(), myComponent.getOperations(), indent, interfaceString);
		interfaceString.append(indent).append("}\n");

		interfaceString.append(indent).append(formatAlias(myComponent.getName())).append(" ..|> ").append("I").append(formatAlias(myComponent.getName())).append(" : implements");
		interfaceString.append("\n");
		return interfaceString.toString();
	}
}
