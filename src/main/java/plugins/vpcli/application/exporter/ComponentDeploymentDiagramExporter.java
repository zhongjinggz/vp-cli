package plugins.vpcli.application.exporter;

import java.util.*;
import java.util.function.Supplier;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.*;

import plugins.vpcli.domain.myuml.uclassifier.*;
import plugins.vpcli.domain.myuml.uclassifier.ComponentData.PortData;
import plugins.vpcli.domain.myuml.upackage.UPackage;
import plugins.vpcli.domain.myuml.ucommon.RelationshipData;
import plugins.vpcli.domain.myuml.udeployment.ArtifactData;

import static com.vp.plugin.diagram.IShapeTypeConstants.*;

public class ComponentDeploymentDiagramExporter extends DiagramExporter {
	
	private final IDiagramUIModel diagram;
	
	private final List<ComponentData> exportedComponents = new ArrayList<>();
	private final List<ClassData> exportedInterfaces = new ArrayList<>();
	private final List<RelationshipData> relationshipDatas = new ArrayList<>();
	private final List<ArtifactData> exportedArtifacts = new ArrayList<>();
    private final List<UPackage> exportedPackages = new ArrayList<>();
	private final List<PortData> allExportedPorts = new ArrayList<>();

	private final Set<String> compModelIds = new HashSet<>();
	private final Set<String> nodeModelIds = new HashSet<>();

	public ComponentDeploymentDiagramExporter(IDiagramUIModel diagram) {
		this.diagram = diagram;
	}
	
	@Override
	public void extract() {
		IDiagramElement[] allElements = diagram.toDiagramElementArray();


		IDiagramElement[] packageDiagramElems = diagram.toDiagramElementArray(SHAPE_TYPE_PACKAGE);
		IDiagramElement[] compDiagramElems = diagram.toDiagramElementArray(SHAPE_TYPE_COMPONENT);
		IDiagramElement[] nodeDiagramElems = diagram.toDiagramElementArray(SHAPE_TYPE_NODE);



		for (IDiagramElement packageElement : packageDiagramElems) {
			String packageModelId = packageElement.getModelElement().getId();
			packageModelIds.add(packageModelId);
		}

		for (IDiagramElement compElement : compDiagramElems) {
			String compeModelId = compElement.getModelElement().getId();
			compModelIds.add(compeModelId);
		}

		for (IDiagramElement nodeElement : nodeDiagramElems) {
			String nodeModelId = nodeElement.getModelElement().getId();
			nodeModelIds.add(nodeModelId);
		}

		List<IRelationship> deferredRelationships = new ArrayList<>();

		for (IDiagramElement diagramElement : allElements) {
			IModelElement modelElement = diagramElement.getModelElement();

			if (modelElement == null) continue;

			allExportedElements.add(modelElement); // Add the model element initially

			if (modelElement instanceof IRelationship) {
				deferredRelationships.add((IRelationship) modelElement); // Defer relationships
			} else if (modelElement instanceof IPort) {
				// Skip ports without warnings
			} else if (!processSupportedElement(modelElement)) {
				allExportedElements.remove(modelElement);
				ApplicationManager.instance().getViewManager().showMessage(
						"Warning: diagram element " + modelElement.getName() + " is of unsupported type and will not be processed ..."
				);
				addWarning("Diagram element " + modelElement.getName() + " is of unsupported type and was not processed.");
			}
		}

		deferredRelationships.forEach(this::extractRelationship);

    }

	private boolean processSupportedElement(IModelElement element) {
		if (element instanceof IComponent) {
			if (isRootLevelInDiagram2(element))
				extractComponent((IComponent) element, null, null);
		} else if (element instanceof IClass) {
			if (isRootLevelInDiagram2(element))
				extractInterface((IClass) element, null);
		} else if (element instanceof INode) {
			if (isRootLevelInDiagram2(element))
				extractNode((INode) element, null, null);
		} else if (element instanceof IArtifact) {
			if (isRootLevelInDiagram2(element))
			    extractArtifact((IArtifact) element, null, null);
		} else if (element instanceof IPackage) {
			extractPackage((IPackage) element);
		} else if (element instanceof INOTE) {
			extractNote((INOTE) element);
		} else {
			return false;
		}
		return true;
	}



	private boolean isRootLevelInDiagram2(IModelElement modelElement) {
		return isRootLevel(modelElement) || !packageModelIds.contains(modelElement.getParent().getId()) || !compModelIds.contains(modelElement.getParent().getId()) || !nodeModelIds.contains(modelElement.getParent().getId());
	}


	private void extractArtifact(IArtifact artifactModel, UPackage uPackage, ComponentData nodeData) {
		boolean isInPackage = (artifactModel.getParent() instanceof IPackage && packageModelIds.contains(artifactModel.getParent().getId()));

		boolean isInNode = (artifactModel.getParent() instanceof INode && nodeModelIds.contains(artifactModel.getParent().getId()));

		ArtifactData artifactData = new ArtifactData(artifactModel.getName(), isInPackage, isInNode);
		artifactData.setDescription(artifactModel.getDescription());
		addSemanticsIfExist(artifactModel, artifactData);

		exportedArtifacts.add(artifactData);
		if (uPackage != null)
			uPackage.getArtifacts().add(artifactData);
		if (nodeData != null)
			nodeData.getArtifacts().add(artifactData);
	}

	private void extractNode(INode nodeModel, UPackage uPackage, ComponentData parentNodeData) {
		boolean isInPackage = (nodeModel.getParent() instanceof IPackage && packageModelIds.contains(nodeModel.getParent().getId()));
		boolean isResident = (nodeModel.getParent() instanceof IComponent && compModelIds.contains(nodeModel.getParent().getId()))

				|| (nodeModel.getParent() instanceof INode && nodeModelIds.contains(nodeModel.getParent().getId()));

		ComponentData nodeData = new ComponentData(nodeModel.getName(), isInPackage);
		nodeData.setNodeComponent(true);

		nodeData.setDescription(nodeModel.getDescription());
		nodeData.setResident(isResident);
		nodeData.setStereotypes(extractStereotypes(nodeModel));

        var nodeIterator = nodeModel.nodeIterator();
		while (nodeIterator.hasNext()) {
			INode nestedNodeModel = (INode) nodeIterator.next();
			extractNode(nestedNodeModel, null, nodeData);
		}

        var artifactIterator = nodeModel.artifactIterator();
		while (artifactIterator.hasNext()) {
			IArtifact residentArtifactModel = (IArtifact) artifactIterator.next();
			extractArtifact(residentArtifactModel, null, nodeData);
		}

        var componentIterator = nodeModel.componentIterator();
		while (componentIterator.hasNext()) {
			IComponent residentComponentModel = (IComponent) componentIterator.next();
			extractComponent(residentComponentModel, null, nodeData);
		}

        var portIterator =  nodeModel.portIterator();
		while (portIterator.hasNext()) {
			IPort portModel = (IPort) portIterator.next();
			PortData portData = new PortData(portModel.getName());
			portData.setId(portModel.getId());

			nodeData.getPorts().add(portData);
			allExportedPorts.add(portData);
		}

		addSemanticsIfExist(nodeModel, nodeData);

		exportedComponents.add(nodeData);
		if (uPackage != null)
			uPackage.getComponents().add(nodeData);
		if (parentNodeData != null)
			parentNodeData.getResidents().add(nodeData);
	}

	private String getPortAliasById(String portID) {
		for (PortData portData : allExportedPorts) {
			if (portData.getId().equals(portID)) {
				return portData.getAlias();
			}
		}
		return null;
	}
	
	private void extractRelationship(IRelationship relationship) {
		IModelElement source = relationship.getFrom();
		IModelElement target = relationship.getTo();
		
		// Checking if the relationship ends are exported (could be unsupported types)
		if (!allExportedElements.contains(source) || !allExportedElements.contains(target)) {
			return;
		}
		
		String sourceName = source.getName();
		String targetName = target.getName();

		if (source instanceof IPort) {
			sourceName = getPortAliasById(source.getId());
		} else if (source instanceof INOTE) {
			sourceName = getNoteAliasById(source.getId());
		} 
		if (target instanceof IPort) {
			targetName = getPortAliasById(target.getId());
		} else if (target instanceof INOTE) {
			targetName = getNoteAliasById(target.getId());
		}

		if (sourceName == null || targetName == null) {
			ApplicationManager.instance().getViewManager()
					.showMessage("Warning: One of the relationship's " +(relationship.getName())+ " elements were null possibly due to illegal relationship (e.g. Anchor between classes) or a hanging connector End");
			addWarning("One of the relationship's elements " + (relationship.getName()) + " were null possibly due to illegal relationship (e.g. Anchor between classes) or a hanging connector End");
			return;
		}


		if (sourceName.isEmpty() && (source instanceof IClass)) {
			sourceName = source.getId();
		} if (targetName.isEmpty() && (target instanceof IClass)) {
			targetName = target.getId();
		}

		if (relationship instanceof IAssociation) {
			IAssociation association = (IAssociation) relationship;

			IAssociationEnd fromEnd = (IAssociationEnd) association.getFromEnd();
			IAssociationEnd toEnd = (IAssociationEnd) association.getToEnd();
			String fromEndMultiplicity = "";
			String toEndMultiplicity = "" ;
			
			if (fromEnd.getMultiplicity() != null) {
				 fromEndMultiplicity = fromEnd.getMultiplicity().equals("Unspecified") ? "" : fromEnd.getMultiplicity();
			}
			if (toEnd.getMultiplicity() != null) {
				 toEndMultiplicity = toEnd.getMultiplicity().equals("Unspecified") ? "" : toEnd.getMultiplicity();
			}
			AssociationData associationData = new AssociationData(sourceName, targetName, relationship.getModelType(),
					relationship.getName(), fromEndMultiplicity, toEndMultiplicity,
					fromEnd.getAggregationKind());
			relationshipDatas.add(associationData);
			return;
		}


		RelationshipData relationshipData = new RelationshipData(sourceName, targetName, relationship.getModelType(),
				relationship.getName());
		relationshipDatas.add(relationshipData);
	}

	private void extractPackage(IPackage packageModel) {
		
		if (isRootLevelInDiagram2(packageModel)) {
			UPackage uPackage = new UPackage(packageModel.getName(), false);
			uPackage.setDescription(packageModel.getDescription());
			IModelElement[] childElements = packageModel.toChildArray();
			for (IModelElement childElement : childElements) {
				if (childElement instanceof IClass) {
					extractInterface((IClass) childElement, uPackage);
				} else if (childElement instanceof IComponent) {
					extractComponent((IComponent) childElement, uPackage, null);
				} else if (childElement instanceof INode) {
					extractNode((INode) childElement, uPackage, null);
				} else if (childElement instanceof IPackage) {
                    extractPackagedPackage((IPackage) childElement, uPackage);
				} else if (childElement instanceof IArtifact) {
					extractArtifact((IArtifact) childElement, uPackage, null);
				}
			}
			addSemanticsIfExist(packageModel, uPackage);
			exportedPackages.add(uPackage);
		}
	}

	private void extractPackagedPackage(IPackage packageModel, UPackage parent) {
		UPackage uPackage = new UPackage(packageModel.getName(), true);
		uPackage.setDescription(packageModel.getDescription());
		IModelElement[] childElements = packageModel.toChildArray();
		for (IModelElement childElement : childElements) {
			if (childElement instanceof IClass) {
				extractInterface((IClass) childElement, uPackage);
			} else if (childElement instanceof IComponent) {
				extractComponent((IComponent) childElement, uPackage, null);
			} else if (childElement instanceof INode) {
				extractNode((INode) childElement, uPackage, null);
			} else if (childElement instanceof IPackage) {
				extractPackagedPackage((IPackage) childElement, uPackage);

			}
		}
		addSemanticsIfExist(packageModel, uPackage);
		parent.getSubPackages().add(uPackage);
		exportedPackages.add(uPackage);
		
	}

	private void extractInterface(IClass interfaceModel, UPackage uPackage) {
		boolean isInPackage = (interfaceModel.getParent() instanceof IPackage && packageModelIds.contains(interfaceModel.getParent().getId()));
		
		ClassData interfaceData = new ClassData(interfaceModel.getName(), isInPackage);
		interfaceData.setDescription(interfaceModel.getDescription());
		interfaceData.setStereotypes(extractStereotypes(interfaceModel));
		interfaceData.setUid(interfaceModel.getId());

		List<AttributeData> attributes = extractAttributes(interfaceModel::attributeIterator);
		List<OperationData> operations = extractOperations(interfaceModel::operationIterator);
		interfaceData.setAttributes(attributes);
		interfaceData.setOperations(operations);

		addSemanticsIfExist(interfaceModel, interfaceData);
		
		exportedInterfaces.add(interfaceData);
		if (uPackage != null)
			uPackage.getClasses().add(interfaceData);
	}

	private void extractComponent(IComponent componentModel, UPackage uPackage, ComponentData parentComponentData) {
		boolean isInPackage = (componentModel.getParent() instanceof IPackage) && packageModelIds.contains(componentModel.getParent().getId());
		boolean isResident = (componentModel.getParent() instanceof IComponent && compModelIds.contains(componentModel.getParent().getId()))

				|| (componentModel.getParent() instanceof INode && nodeModelIds.contains(componentModel.getParent().getId()));
		
		ComponentData componentData = new ComponentData(componentModel.getName(), isInPackage);
		componentData.setDescription(componentModel.getDescription());
		componentData.setResident(isResident);
		
		componentData.setStereotypes(extractStereotypes(componentModel));

        var componentIterator = componentModel.componentIterator();
		while (componentIterator.hasNext()) {
			IComponent residentComponentModel = (IComponent) componentIterator.next();
			extractComponent(residentComponentModel, null, componentData);  // idk
		}

        var portIterator =  componentModel.portIterator();
		while (portIterator.hasNext()) {
			IPort portModel = (IPort) portIterator.next();
			PortData portData = new PortData(portModel.getName());
			portData.setId(portModel.getId());
			
			componentData.getPorts().add(portData);
			allExportedPorts.add(portData);
		}

		componentData.setAttributes(extractAttributes(componentModel::attributeIterator));
		componentData.setOperations(extractOperations(componentModel::operationIterator));

		addSemanticsIfExist(componentModel, componentData);
		
		exportedComponents.add(componentData);
		if (uPackage != null)
			uPackage.getComponents().add(componentData);
		if (parentComponentData != null)
			parentComponentData.getResidents().add(componentData);

	}


	private List<AttributeData> extractAttributes(Supplier<Iterator> attributeIteratorSupplier) {
		List<AttributeData> attributes = new ArrayList<>();
        var attributeIter = attributeIteratorSupplier.get();
		while (attributeIter.hasNext()) {
			IAttribute attribute = (IAttribute) attributeIter.next();
			AttributeData attr = new AttributeData(attribute.getVisibility(), attribute.getName(),
					attribute.getTypeAsString(), attribute.getInitialValueAsString(), attribute.getScope());

			attributes.add(attr);
		}
		return attributes;
	}

	private List<OperationData> extractOperations(Supplier<Iterator> operationIteratorSupplier) {
		List<OperationData> operations = new ArrayList<>();
        var operationIter = operationIteratorSupplier.get();
		while (operationIter.hasNext()) {
			IOperation operation = (IOperation) operationIter.next();
			OperationData op = new OperationData(operation.getVisibility(), operation.getName(),
					operation.getReturnTypeAsString(), operation.isAbstract(), null, operation.getScope());

            var paramIterator = operation.parameterIterator();
			while (paramIterator.hasNext()) {
				IParameter parameter = (IParameter) paramIterator.next();
				OperationData.Parameter paramData = new OperationData.Parameter(parameter.getName(),
						parameter.getTypeAsString(), parameter.getDefaultValueAsString());
				op.addParameter(paramData);
			}
			operations.add(op);
		}
		return operations;
	}

	public List<ComponentData> getExportedComponents() {
		return exportedComponents;
	}
	
	public List<ClassData> getExportedInterfaces() {
		return exportedInterfaces;
	}
	
	public List<UPackage> getExportedPackages() {
		return exportedPackages;
	}
	public List<RelationshipData> getRelationshipDatas() {
		return relationshipDatas;
	}

	public List<ArtifactData> getExportedArtifacts() {
		return exportedArtifacts;
	}

}
