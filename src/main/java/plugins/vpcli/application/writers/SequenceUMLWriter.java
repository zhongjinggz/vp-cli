package plugins.vpcli.application.writers;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.vp.plugin.ApplicationManager;

import plugins.vpcli.domain.myuml.mycommon.MyNote;
import plugins.vpcli.domain.myuml.mycommon.MyRelationship;
import plugins.vpcli.domain.myuml.myinteraction.MyCombinedFragment;
import plugins.vpcli.domain.myuml.myinteraction.MyInteractionRef;
import plugins.vpcli.domain.myuml.myinteraction.MyLifeline;
import plugins.vpcli.domain.myuml.myinteraction.MessageData;
import plugins.vpcli.domain.myuml.myusecase.MyActor;

public class SequenceUMLWriter extends PlantUMLWriter {

	private final List<MyActor> actors;
	private final List<MyLifeline> lifelines;
	private final List<MessageData> messages;
	private final List<MyCombinedFragment> fragments;
	private final List<MyInteractionRef> refs;
	private final List<MyRelationship> anchors;

	private final Set<String> activatedLifelines = new HashSet<>();

	public SequenceUMLWriter(List<MyNote> notes, List<MyActor> actors, List<MyLifeline> lifelines,
							 List<MessageData> messages, List<MyCombinedFragment> fragments, List<MyInteractionRef> refs, List<MyRelationship> anchors) {
		super(notes);
		this.actors = actors;
		this.lifelines = lifelines;
		this.messages = messages;
		this.fragments = fragments;
		this.refs = refs;
		this.anchors = anchors;
	}

	@Override
	public void writeToFile(File file) throws IOException {
		StringBuilder plantUMLContent = new StringBuilder("@startuml\n");

		for (MyActor myActor : actors) {
			plantUMLContent.append(writeActor(myActor, ""));
		}

		for (MyLifeline myLifeline : lifelines) {
			// if it is created by a message we hold until that message is about to be written for proper puml syntax
			if (!myLifeline.isCreatedByMessage())	plantUMLContent.append(writeLifeline(myLifeline, ""));
		}
		for (MessageData messageData : messages) {
			plantUMLContent.append(writeMessage(messageData, ""));
		}
		for (MyCombinedFragment fragment : fragments) {
			plantUMLContent.append(writeFragment(fragment));
		}

		for (MyInteractionRef ref : refs) {
			plantUMLContent.append(writeRef(ref));
		}
		
		plantUMLContent.append(writeNotes());
		
		for (MyRelationship anchor : anchors) {
			plantUMLContent.append(anchor.toExportFormat());
		}

		plantUMLContent.append("@enduml");
		try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8)) {
			writer.write(plantUMLContent.toString());
		}
	}

	private String writeRef(MyInteractionRef ref) {
		
		if (ref.getCoveredLifelines() == null || ref.getCoveredLifelines().isEmpty()) return "";

        return "ref over " +
            String.join(", ", ref.getCoveredLifelines()) +
            " : " +
            ref.getRefName() +
            "\n";
	}

	private String writeFragment(MyCombinedFragment fragment) {
		StringBuilder fragmentString = new StringBuilder();

		String fragmentType = fragment.getType();
		if (fragmentType.equals("alt") || fragmentType.equals("opt") || fragmentType.equals("loop") ||
		    fragmentType.equals("par") || fragmentType.equals("break") || fragmentType.equals("critical")) {
		    
		    fragmentString.append(fragmentType).append("\n\n");

		    List<MyCombinedFragment.Operand> operands = fragment.getOperands();
		    boolean isFirstOperand = true; 

		    for (MyCombinedFragment.Operand operand : operands) {
		        if (!isFirstOperand) {
		            fragmentString.append("else\n\n");
		        }

		        for (MessageData messageData : operand.getMessages()) {
		            fragmentString.append(writeMessage(messageData, "\t"));
		        }

		        isFirstOperand = false; 
		    }

		    fragmentString.append("end\n");
		} else {
			ApplicationManager.instance().getViewManager().showMessage("Combined fragments of type " + fragmentType + " have no PlantUML equivalent");
			return "";
		}
		return fragmentString.toString();
	}

	private String writeMessage(MessageData messageData, String indent) {
		boolean activate = false;
		String lifelineString = "";
		if (!activatedLifelines.contains(messageData.getTarget())) {
			activate = true;
			activatedLifelines.add(messageData.getTarget());
		}

		if (messageData.isCreate()) lifelineString = "create " + writeLifeline(messageData.getCreatedLifeline(), indent);
		return lifelineString + messageData.toExportFormat(activate, indent);
	}

	private String writeLifeline(MyLifeline myLifeline, String indent) {
		StringBuilder lifelineString = new StringBuilder();
		String name = myLifeline.getName();

		String aliasDeclaration = formatAlias(myLifeline.getName()).equals(myLifeline.getName()) ? "" : (" as " + formatAlias(myLifeline.getName()));

		String declaration = "participant";

		if (myLifeline.getStereotypes().contains("control")) {
			declaration = "control";
		} else if (myLifeline.getStereotypes().contains("entity")) {
			declaration = "entity";
		} else if (myLifeline.getStereotypes().contains("boundary")) {
			declaration = "boundary";
		}

		lifelineString.append(indent).append(declaration).append(" ").append(formatName(name)).append(aliasDeclaration);

		if (!myLifeline.getStereotypes().isEmpty()) {
			String stereotypesString = myLifeline.getStereotypes().stream()
					.filter(stereotype -> !"control".equals(stereotype) && !"entity".equals(stereotype) && !"boundary".equals(stereotype))
					.map(stereotype -> "<<" + stereotype + ">>")
					.collect(Collectors.joining(", "));
			if (!stereotypesString.isEmpty()) {
				lifelineString.append(" ").append(stereotypesString);
			}
		}
		lifelineString.append("\n");
		if (myLifeline.getClassifier() != null && !myLifeline.getClassifier().isEmpty()) {
			lifelineString.append("note over ").append(formatAlias(myLifeline.getName())).append(" : ").append("Classifier: ").append(myLifeline.getClassifier());
			lifelineString.append("\n");
		}
		return lifelineString.toString();
	}

	private String writeActor(MyActor myActor, String indent) {
		StringBuilder actorString = new StringBuilder();
		String name = myActor.getName();

		String aliasDeclaration = formatAlias(myActor.getName()).equals(myActor.getName()) ? "" : (" as " + formatAlias(myActor.getName()));
		actorString.append(indent).append("actor ").append(formatName(name)).append(aliasDeclaration);


		if (!myActor.getStereotypes().isEmpty()) {
			String stereotypesString = myActor.getStereotypes().stream().map(stereotype -> "<<" + stereotype + ">>")
					.collect(Collectors.joining(", "));
			actorString.append(" ").append(stereotypesString);
		}
		actorString.append("\n");
		return actorString.toString();
	}
}