package plugins.vpcli.application.writers;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;


import plugins.vpcli.domain.myuml.uusecase.ActorData;
import plugins.vpcli.domain.myuml.ucommon.NoteData;
import plugins.vpcli.domain.myuml.upackage.UPackage;
import plugins.vpcli.domain.myuml.ucommon.RelationshipData;
import plugins.vpcli.domain.myuml.uusecase.UseCaseData;

public class UseCaseWriter extends PlantUMLWriter {
    
	private final List<UPackage> packages;
    private final List<ActorData> actors;
    private final List<RelationshipData> relationships;
    private final List<UseCaseData> useCases;

    public UseCaseWriter(List<UseCaseData> useCases, List<RelationshipData> relationships, List<UPackage> packages, List<ActorData> actors, List<NoteData> notes) {
    	super(notes);
    	this.packages = packages;
    	this.useCases = useCases;
    	this.actors = actors;
        this.relationships = relationships;
    }

    public void writeToFile(File file) throws IOException {
        StringBuilder plantUMLContent = new StringBuilder("@startuml\n");
        
        for (UPackage uPackage : packages) {
        	if(!uPackage.isSubpackage())
        		plantUMLContent.append(writePackage(uPackage, ""));
        }

        for (ActorData actorData : actors) {
        	if(!actorData.isInPackage())  
        		plantUMLContent.append(writeActor(actorData, ""));
        }

        for (UseCaseData usecaseData : useCases) {
        	if(!usecaseData.isInPackage())  
        		plantUMLContent.append(writeUseCase(usecaseData, ""));
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
    
    

	private String writePackage(UPackage uPackage, String indent) {
    	StringBuilder packageString = new StringBuilder();
    	String name = formatName(uPackage.getName());
    	String definition = uPackage.isRectangle() ? "rectangle " : "package " ;
    	packageString.append(indent).append(definition).append(name).append(" {\n");
    	
    	for (ActorData actorData : uPackage.getActors()) {
    		packageString.append(writeActor(actorData, indent + "\t"));
    	}
    	for (UseCaseData useCaseData : uPackage.getUseCases()) {
    		packageString.append(writeUseCase(useCaseData, indent + "\t"));
    	}
    	
    	for (UPackage subPackage : uPackage.getSubPackages()) {
    		packageString.append(writePackage(subPackage, indent + "\t"));
    		
    	}
    	
    	packageString.append(indent).append("}\n");
		return packageString.toString();
    }


	private String writeUseCase(UseCaseData useCaseData, String indent) {
	    StringBuilder usecaseString = new StringBuilder();
	    String name = useCaseData.getName();
	    String business = useCaseData.isBusiness() ? "/" : "";
		String aliasDeclaration = formatAlias(useCaseData.getName()).equals(useCaseData.getName()) ? "" : (" as " + formatAlias(useCaseData.getName()));
	    usecaseString.append(indent).append("usecase").append(business)
	                 .append(" (").append(name).append(")").append(aliasDeclaration);
	    
	    if (!useCaseData.getStereotypes().isEmpty()) {
	        String stereotypesString = useCaseData.getStereotypes().stream()
	            .filter(stereotype -> !"UseCase".equals(stereotype)) // Exclude "UseCase", VP auto applies it to every use case for some reason
	            .map(stereotype -> "<<" + stereotype + ">>")
	            .collect(Collectors.joining(", "));
	        if (!stereotypesString.isEmpty()) { 
	            usecaseString.append(" ").append(stereotypesString);
	        }
	    }
	    
	    usecaseString.append("\n");
	    return usecaseString.toString();
	}


	private String writeActor(ActorData actorData, String indent) {
		StringBuilder actorString = new StringBuilder();
		String name = actorData.getName();
		String aliasDeclaration = formatAlias(actorData.getName()).equals(actorData.getName()) ? "" : (" as " + formatAlias(actorData.getName()));
		String business = actorData.isBusiness() ? "/" : "";
		actorString.append(indent).append("actor").append(business).append(" ").append(" :").append(name).append(":").append(aliasDeclaration);
		if (!actorData.getStereotypes().isEmpty()) {
	        String stereotypesString = actorData.getStereotypes().stream()
	            .map(stereotype -> "<<" + stereotype + ">>")
	            .collect(Collectors.joining(", "));
	        actorString.append(" ").append(stereotypesString);
	    }
		actorString.append("\n");
		return actorString.toString();
	}

	private String writeRelationship(RelationshipData relationship) {
		return relationship.toExportFormat();
    }

}
