package plugins.vpcli.application.writers;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;


import plugins.vpcli.domain.myuml.myusecase.MyActor;
import plugins.vpcli.domain.myuml.mycommon.MyNote;
import plugins.vpcli.domain.myuml.mypackage.MyPackage;
import plugins.vpcli.domain.myuml.mycommon.MyRelationship;
import plugins.vpcli.domain.myuml.myusecase.MyUseCase;

public class UseCaseWriter extends PlantUMLWriter {
    
	private final List<MyPackage> packages;
    private final List<MyActor> actors;
    private final List<MyRelationship> relationships;
    private final List<MyUseCase> useCases;

    public UseCaseWriter(List<MyUseCase> useCases, List<MyRelationship> relationships, List<MyPackage> packages, List<MyActor> actors, List<MyNote> notes) {
    	super(notes);
    	this.packages = packages;
    	this.useCases = useCases;
    	this.actors = actors;
        this.relationships = relationships;
    }

    public void writeToFile(File file) throws IOException {
        StringBuilder plantUMLContent = new StringBuilder("@startuml\n");
        
        for (MyPackage uPackage : packages) {
        	if(!uPackage.isSubpackage())
        		plantUMLContent.append(writePackage(uPackage, ""));
        }

        for (MyActor myActor : actors) {
        	if(!myActor.isInPackage())
        		plantUMLContent.append(writeActor(myActor, ""));
        }

        for (MyUseCase usecaseU : useCases) {
        	if(!usecaseU.isInPackage())
        		plantUMLContent.append(writeUseCase(usecaseU, ""));
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
    
    

	private String writePackage(MyPackage uPackage, String indent) {
    	StringBuilder packageString = new StringBuilder();
    	String name = formatName(uPackage.getName());
    	String definition = uPackage.isRectangle() ? "rectangle " : "package " ;
    	packageString.append(indent).append(definition).append(name).append(" {\n");
    	
    	for (MyActor myActor : uPackage.getActors()) {
    		packageString.append(writeActor(myActor, indent + "\t"));
    	}
    	for (MyUseCase uUseCase : uPackage.getUseCases()) {
    		packageString.append(writeUseCase(uUseCase, indent + "\t"));
    	}
    	
    	for (MyPackage subPackage : uPackage.getSubPackages()) {
    		packageString.append(writePackage(subPackage, indent + "\t"));
    		
    	}
    	
    	packageString.append(indent).append("}\n");
		return packageString.toString();
    }


	private String writeUseCase(MyUseCase uUseCase, String indent) {
	    StringBuilder usecaseString = new StringBuilder();
	    String name = uUseCase.getName();
	    String business = uUseCase.isBusiness() ? "/" : "";
		String aliasDeclaration = formatAlias(uUseCase.getName()).equals(uUseCase.getName()) ? "" : (" as " + formatAlias(uUseCase.getName()));
	    usecaseString.append(indent).append("usecase").append(business)
	                 .append(" (").append(name).append(")").append(aliasDeclaration);
	    
	    if (!uUseCase.getStereotypes().isEmpty()) {
	        String stereotypesString = uUseCase.getStereotypes().stream()
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


	private String writeActor(MyActor myActor, String indent) {
		StringBuilder actorString = new StringBuilder();
		String name = myActor.getName();
		String aliasDeclaration = formatAlias(myActor.getName()).equals(myActor.getName()) ? "" : (" as " + formatAlias(myActor.getName()));
		String business = myActor.isBusiness() ? "/" : "";
		actorString.append(indent).append("actor").append(business).append(" ").append(" :").append(name).append(":").append(aliasDeclaration);
		if (!myActor.getStereotypes().isEmpty()) {
	        String stereotypesString = myActor.getStereotypes().stream()
	            .map(stereotype -> "<<" + stereotype + ">>")
	            .collect(Collectors.joining(", "));
	        actorString.append(" ").append(stereotypesString);
	    }
		actorString.append("\n");
		return actorString.toString();
	}

	private String writeRelationship(MyRelationship relationship) {
		return relationship.toExportFormat();
    }

}
