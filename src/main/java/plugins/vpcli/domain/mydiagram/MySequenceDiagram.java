package plugins.vpcli.domain.mydiagram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.*;

import plugins.vpcli.domain.myuml.mycommon.MyRelationship;
import plugins.vpcli.domain.myuml.myinteraction.MyCombinedFragment;
import plugins.vpcli.domain.myuml.myinteraction.MyInteractionRef;
import plugins.vpcli.domain.myuml.myinteraction.MyLifeline;
import plugins.vpcli.domain.myuml.myinteraction.MessageData;
import plugins.vpcli.domain.myuml.myusecase.MyActor;

public class MySequenceDiagram extends MyDiagram {

	private final IDiagramUIModel diagram;

	private final List<MyActor> exportedInteractionActors = new ArrayList<>();
    private final List<MyLifeline> exportedLifelines = new ArrayList<>();
	private final List<MessageData> exportedMessages = new ArrayList<>();
	private final List<MyCombinedFragment> exportedFragments = new ArrayList<>();
	private final List<MyInteractionRef> exportedRefs = new ArrayList<>();
	private final List<MyRelationship> exportedAnchors = new ArrayList<>();

	private final Map<IInteractionLifeLine, MyLifeline> lifelineMap = new HashMap<>();
	private final Set<IMessage> processedMessages = new HashSet<>(); // Set to track processed messages

	public MySequenceDiagram(IDiagramUIModel diagram) {
		this.diagram = diagram;
	}

	@Override
	public void extract() {
		IDiagramElement[] allElements = diagram.toDiagramElementArray();

		List<IMessage> iMessages = new ArrayList<>();
		List<ICombinedFragment> iCombinedFragments = new ArrayList<>();
		List<IAnchor> iAnchors = new ArrayList<>();

		for (IDiagramElement diagramElement : allElements) {
			IModelElement modelElement = diagramElement.getModelElement();

			if (modelElement == null) {
				ApplicationManager.instance().getViewManager()
						.showMessage("Warning: modelElement is null for a diagram element.");
				addWarning("ModelElement is null for a diagram element.");
				continue;
			}
			// Add to exported elements preemptively
			allExportedElements.add(modelElement);

			if (modelElement instanceof IInteractionActor) {
				extractInteractionActor((IInteractionActor) modelElement);
			} else if (modelElement instanceof IInteractionLifeLine) {
				extractLifeline((IInteractionLifeLine) modelElement);
			} else if (modelElement instanceof IInteractionOccurrence) {
				extracRef((IInteractionOccurrence) modelElement);
			} else if (modelElement instanceof INOTE) {
				extractNote((INOTE) modelElement);
			} else if (modelElement instanceof IMessage) {
				iMessages.add((IMessage) modelElement); // Defer processing messages
			} else if (modelElement instanceof ICombinedFragment) {
				iCombinedFragments.add((ICombinedFragment) modelElement); // Defer processing fragments
			} else if (modelElement instanceof IAnchor) {
				iAnchors.add((IAnchor) modelElement); // Defer processing anchors
			} else {
				allExportedElements.remove(modelElement);

				if (!(modelElement instanceof IActivation)) {
					ApplicationManager.instance().getViewManager().showMessage(
							"Warning: diagram element " + modelElement.getName()
									+ " is of unsupported type and will not be processed ..."
					);
					addWarning("Diagram element " + modelElement.getName()
							+ " is of unsupported type and will not be processed ...");
				}

			}
		}

		iCombinedFragments.forEach(this::extractFragment);

		for (IMessage message : iMessages) {
			if (!processedMessages.contains(message)) { // Check if message is already processed
				exportedMessages.add(extractMessage(message));
			}
		}

        iAnchors.forEach(this::extractAnchor);
	}

	private void extractAnchor(IRelationship relationship) {
		IModelElement source = relationship.getFrom();
		IModelElement target = relationship.getTo();
//		ApplicationManager.instance().getViewManager().showMessage("rel type? " + relationship.getModelType());
		String sourceName = source.getName();
		String targetName = target.getName();

		if (source instanceof INOTE) {
			sourceName = getNoteAliasById(source.getId());

		} else if (target instanceof INOTE) {
			targetName = getNoteAliasById(target.getId());
		} 
		if (sourceName == null || targetName == null) {
			ApplicationManager.instance().getViewManager()
			.showMessage("Warning: One of the Anchor's elements were null possibly due to illegal relationship (e.g. an Anchor between classes)");
			addWarning("One of the Anchor's elements were null possibly due to illegal relationship (e.g. an Anchor between classes)");
			return;
		}
		MyRelationship myRelationship = new MyRelationship(sourceName, targetName, relationship.getModelType(),
				relationship.getName());
		exportedAnchors.add(myRelationship);
	}

	private void extracRef(IInteractionOccurrence refModel) {
		if(refModel.getRefersTo() == null) {
			ApplicationManager.instance().getViewManager()
			.showMessage("A ref referring to nothing was skipped.");
			addWarning("A ref referring to nothing was skipped.");
			return;
		}
		String referenceName = refModel.getRefersTo().getName();
		MyInteractionRef ref = new MyInteractionRef(referenceName);


		for(IModelElement coveredLifeLine : refModel.toCoveredLifeLineArray()) {
			ref.getCoveredLifelines().add(coveredLifeLine.getName());
		}
		exportedRefs.add(ref);
	}

	private void extractFragment(ICombinedFragment fragmentModel) {
		MyCombinedFragment fragment = new MyCombinedFragment(fragmentModel.getInteractionOperator());

		for(IInteractionOperand childOperand : fragmentModel.toOperandArray()) {

			MyCombinedFragment.Operand operand = new MyCombinedFragment.Operand();
			if (childOperand.toMessageArray() != null) {
				for (IMessage message : childOperand.toMessageArray()) {

					operand.getMessages().add(extractMessage(message));
					processedMessages.add(message); // Mark message as processed, avoid duplication 
				}
			}
			fragment.getOperands().add(operand);
		}
		exportedFragments.add(fragment);
	}

	private MessageData extractMessage(IMessage messageModel) {
		IModelElement source = messageModel.getFrom();
		IModelElement target = messageModel.getTo();

		String sourceName = (source == null) ? "[" : source.getName();
		String targetName = (target == null) ? "]" : target.getName();

		if (sourceName.isEmpty() && source instanceof IInteractionLifeLine) {
			sourceName = ((IInteractionLifeLine) source).getBaseClassifierAsModel().getName();
		}
		if (targetName.isEmpty() && target instanceof IInteractionLifeLine) {
			targetName = ((IInteractionLifeLine) target).getBaseClassifierAsModel().getName();
		}
		MessageData messageData = new MessageData(sourceName, targetName, "Message", messageModel.getName());

		if (messageModel.getType() == IMessage.TYPE_CREATE_MESSAGE) {
			MyLifeline createMyLifeline = lifelineMap.get((IInteractionLifeLine) target);
			createMyLifeline.setCreatedByMessage(true);
			messageData.setCreate(true, createMyLifeline);
		} else if (messageModel.getType() == IMessage.TYPE_DURATION_MESSAGE) {
			int durationHeight = messageModel.getDurationHeight();
			messageData.setDuration(durationHeight);
		} else if (messageModel.getType() == IMessage.TYPE_RECURSIVE_MESSAGE) {
			messageData.setRecursive(true);
		}

		IModelElement actionType = messageModel.getActionType();
		if (actionType != null) {
			if (actionType.getName().equals("Return"))
				messageData.setReply(true);
			else if (actionType.getName().equals("Destroy")) {
				messageData.setDestroy(true);
			} else if (actionType.getName().equals("Call")) {
				IModelElement operation = ((IActionTypeCall) actionType).getOperation();
				if (operation != null) messageData.setName(operation.getName() + "()");
			}
		}

		messageData.setSequenceNumber(messageModel.getSequenceNumber());
		return messageData;
	}

	private void extractLifeline(IInteractionLifeLine lifelineModel) {
		String name = lifelineModel.getName();

		MyLifeline myLifeline = new MyLifeline(name);
		myLifeline.setDescription(lifelineModel.getDescription());
		IModelElement classifierModel = lifelineModel.getBaseClassifierAsModel();
		if (classifierModel != null) {
			myLifeline.setClassifier(classifierModel.getName());
			if (name.isEmpty()) myLifeline.setName(classifierModel.getName());
		}


		myLifeline.setStereotypes(extractStereotypes(lifelineModel));
		exportedLifelines.add(myLifeline);
		lifelineMap.put(lifelineModel, myLifeline);
	}

	private void extractInteractionActor(IInteractionActor modelElement) {
		String name = modelElement.getName();
		MyActor myActor = new MyActor(name);
		myActor.setStereotypes(extractStereotypes(modelElement));
		myActor.setDescription(modelElement.getDescription());
		exportedInteractionActors.add(myActor);
	}

	public List<MyActor> getExportedInteractionActors() {
		return exportedInteractionActors;
	}

	public List<MyLifeline> getExportedLifelines() {
		return exportedLifelines;
	}

	public List<MessageData> getExportedMessages() {
		return exportedMessages;
	}

	public List<MyCombinedFragment> getExportedFragments() {
		return exportedFragments;
	}

	public List<MyInteractionRef> getExportedRefs() {
		return exportedRefs;
	}
	
	public List<MyRelationship> getExportedAnchors() {
		return exportedAnchors;
	}
}
