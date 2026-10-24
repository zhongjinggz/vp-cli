package plugins.vpcli.application.writers_old;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;


import plugins.vpcli.domain.myuml_old.myusecase_old.MyActor_old;
import plugins.vpcli.domain.myuml_old.mycommon_old.MyNote_old;
import plugins.vpcli.domain.myuml.mypackage.MyPackage;
import plugins.vpcli.domain.myuml_old.mycommon_old.MyRelationship_old;
import plugins.vpcli.domain.myuml.myusecase.MyUseCase;

public class UseCaseWriter extends PlantUMLWriter {
    
	private final List<MyPackage> packages;
    private final List<MyActor_old> actors;
    private final List<MyRelationship_old> relationships;
    private final List<MyUseCase> useCases;

    public UseCaseWriter(List<MyUseCase> useCases, List<MyRelationship_old> relationships, List<MyPackage> packages, List<MyActor_old> actors, List<MyNote_old> notes) {
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

        for (MyActor_old myActor : actors) {
        	if(!myActor.isInPackage())
        		plantUMLContent.append(writeActor(myActor, ""));
        }

        for (MyUseCase usecaseU : useCases) {
//        	if(!usecaseU.isInPackage())
//        		plantUMLContent.append(writeUseCase(usecaseU, ""));
        }
        
        plantUMLContent.append(writeNotes());

        for (MyRelationship_old relationship : relationships) {
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
    	
//    	for (MyActor_old myActor : uPackage.getActors()) {
//    		packageString.append(writeActor(myActor, indent + "\t"));
//    	}
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
//	    String business = uUseCase.isBusiness() ? "/" : "";
		String aliasDeclaration = formatAlias(uUseCase.getName()).equals(uUseCase.getName()) ? "" : (" as " + formatAlias(uUseCase.getName()));
//	    usecaseString.append(indent).append("usecase").append(business)
//	                 .append(" (").append(name).append(")").append(aliasDeclaration);
//
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


	private String writeActor(MyActor_old myActor, String indent) {
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

	private String writeRelationship(MyRelationship_old relationship) {
		return relationship.toExportFormat();
    }

}
