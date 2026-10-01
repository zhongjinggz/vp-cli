package plugins.vpcli.application.exporter;

import java.util.*;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.connector.IContainmentUIModel;
import com.vp.plugin.model.*;

import plugins.vpcli.domain.myuml.uclassifier.AssociationData;
import plugins.vpcli.domain.myuml.uclassifier.AttributeData;
import plugins.vpcli.domain.myuml.uclassifier.UClass;
import plugins.vpcli.domain.myuml.uclassifier.NaryData;
import plugins.vpcli.domain.myuml.uclassifier.OperationData;
import plugins.vpcli.domain.myuml.uclassifier.OperationData.Parameter;
import plugins.vpcli.domain.myuml.upackage.UPackage;
import plugins.vpcli.domain.myuml.ucommon.RelationshipData;

import static com.vp.plugin.diagram.IShapeTypeConstants.SHAPE_TYPE_PACKAGE;

public class ClassDiagramExporter extends DiagramExporter {

	private final IDiagramUIModel diagram;

	private final List<UClass> exportedClasses = new ArrayList<>();
	private final List<RelationshipData> relationshipDatas = new ArrayList<>();
	private final List<UPackage> exportedPackages = new ArrayList<>();
	private final List<NaryData> exportedNary = new ArrayList<>();


    private final List<NaryData> allExportedNary = new ArrayList<>();

	public ClassDiagramExporter(IDiagramUIModel diagram) {
		this.diagram = diagram;
	}

	@Override
	public void extract() {
		IDiagramElement[] allElements = diagram.toDiagramElementArray();

		IDiagramElement[] packageDiagramElems = diagram.toDiagramElementArray(SHAPE_TYPE_PACKAGE);
		for (IDiagramElement packageElement : packageDiagramElems) {
			String packageModelId = packageElement.getModelElement().getId();
			packageModelIds.add(packageModelId);
		}


		List<IRelationship> deferredRelationships = new ArrayList<>();

		for (IDiagramElement diagramElement : allElements) {


			IModelElement modelElement = diagramElement.getModelElement();

			if (modelElement == null) {
				ApplicationManager.instance().getViewManager()
						.showMessage("Warning: modelElement is null for a diagram element.");
				addWarning("ModelElement is null for a diagram element.");
				continue;
			}

			// Add to exported elements list
			allExportedElements.add(modelElement);

			if (modelElement instanceof IClass) {
				if (isRootLevelInDiagram(modelElement)) {
						extractClass((IClass) modelElement, null);
				}
			} else if (modelElement instanceof IPackage) {
				extractPackage((IPackage) modelElement);
			} else if (modelElement instanceof INARY) {
				if (isRootLevelInDiagram(modelElement)) {
					extractNary((INARY) modelElement, null);
				}
			} else if (modelElement instanceof INOTE) {
				extractNote((INOTE) modelElement);
			} else if (modelElement instanceof IRelationship) {
				deferredRelationships.add((IRelationship) modelElement); // Defer relationships
			} else {
				allExportedElements.remove(modelElement);
				ApplicationManager.instance().getViewManager()
						.showMessage("Warning: diagram element " + modelElement.getName()
								+ " is of unsupported type and will not be processed ... ");

				addWarning("Diagram element " + modelElement.getName()
								+ " is of unsupported type and was not processed. ");
			}
		}

		for (IDiagramElement diagramElement : allElements) {
			if (diagramElement instanceof IContainmentUIModel) {
				extractContainment((IContainmentUIModel) diagramElement);
			}
		}

		for (IRelationship relationship : deferredRelationships) {
			extractRelationship(relationship);
		}

    }



	private void extractClass(IClass classModel, UPackage uPackage) {
		boolean isInPackage = !isRootLevelInDiagram(classModel);
		UClass uClass = new UClass(classModel.getName(), classModel.isAbstract(), classModel.getVisibility(),
				isInPackage);
		uClass.setDescription(classModel.getDescription());
		uClass.setStereotypes(extractStereotypes(classModel));
		extractAttributes(classModel, uClass);
		extractOperations(classModel, uClass);
		
		exportedClasses.add(uClass);
		if (uPackage != null)
			uPackage.getClasses().add(uClass);
	}

	private void extractNary(INARY naryModel, UPackage uPackage) {
		boolean isInPackage = !isRootLevelInDiagram(naryModel);
		String name = naryModel.getName();
		String id = naryModel.getId();
		NaryData naryData = new NaryData(name, id, isInPackage);
		naryData.setDescription(naryModel.getDescription());

		if (uPackage != null)
			uPackage.getNaries().add(naryData);
		else exportedNary.add(naryData); // I changed if bug

		allExportedNary.add(naryData); // Naries are to be reversed by id so whether in package or not, need to add so that relationships aren't pointing to null.
	}


	private String getNaryAliasById(String naryId) {
		for (NaryData naryData : allExportedNary) {
			if (naryData.getId().equals(naryId)) {
				return naryData.getAlias();
			}
		}
		return null;
	}

	private void extractContainment(IContainmentUIModel relationship) {
		IModelElement source = relationship.getFromShape().getModelElement();
		IModelElement target = relationship.getToShape().getModelElement();
		String sourceName = source.getName();
		String targetName = target.getName();
		RelationshipData relationshipData = new RelationshipData(sourceName, targetName, "Containment", "");
		relationshipDatas.add(relationshipData);
	}

	private void extractRelationship(IRelationship relationship) {
		IModelElement source = relationship.getFrom();
		IModelElement target = relationship.getTo();
//		ApplicationManager.instance().getViewManager().showMessage("rel type? " + relationship.getModelType());
		if (!allExportedElements.contains(source) || !allExportedElements.contains(target)) {
			return;
		}
		if (source.getName() == null || target.getName() == null) {
			ApplicationManager.instance().getViewManager()
					.showMessage("Warning: One of the relationship's " +(relationship.getName())+ " elements were null possibly due to illegal relationship (e.g. Anchor between classes) or a hanging connector End");
			addWarning("One of the relationship's elements " + (relationship.getName()) + " were null possibly due to illegal relationship (e.g. Anchor between classes) or a hanging connector End");
			return;
		}

		String sourceName = source.getName();
		String targetName = target.getName();

		if (source instanceof INARY) {
			sourceName = getNaryAliasById(source.getId());
		} else if (source instanceof INOTE) {
			sourceName = getNoteAliasById(source.getId());
		} 
		if (target instanceof INARY) {
			targetName = getNaryAliasById(target.getId());
		} else if (target instanceof INOTE) {
			targetName = getNoteAliasById(target.getId());
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
					relationship.getName(), fromEndMultiplicity, toEndMultiplicity, fromEnd.getAggregationKind());
			relationshipDatas.add(associationData);
			return;
		}
		if (relationship instanceof IAssociationClass) {
			
			IAssociation association;
			
				if (source instanceof IAssociation) {
				association = (IAssociation) source;
				String associationFrom = formatAlias(association.getFrom().getName());
				String associationTo = formatAlias(association.getTo().getName());
				sourceName = "(" + associationFrom + ", " + associationTo + ")";
			} else {
				association = (IAssociation) target;
				String associationFrom = association.getFrom().getName();
				String associationTo = association.getTo().getName();
				targetName = "(" + associationFrom + ", " + associationTo + ")";
			}
		}

		RelationshipData relationshipData = new RelationshipData(sourceName, targetName, relationship.getModelType(), relationship.getName());
		relationshipDatas.add(relationshipData);
	}

	private void extractPackage(IPackage packageModel) {

		if (isRootLevelInDiagram(packageModel)) {
			UPackage uPackage = new UPackage(packageModel.getName(), null, null, null, false, false);
			uPackage.setDescription(packageModel.getDescription());
			IModelElement[] childElements = packageModel.toChildArray();
			for (IModelElement childElement : childElements) {
				if (childElement instanceof IClass) {
					extractClass((IClass) childElement, uPackage);
				} else if (childElement instanceof INARY) {
					extractNary((INARY) childElement, uPackage);
				} else if (childElement instanceof IPackage) {
                    extractPackagedPackage((IPackage) childElement, uPackage);
				}
			}
			exportedPackages.add(uPackage);
		}
	}

	private void extractPackagedPackage(IPackage packageModel, UPackage parent) {

		UPackage uPackage = new UPackage(packageModel.getName(), null, null, null, true, false);
		uPackage.setDescription(packageModel.getDescription());
		IModelElement[] childElements = packageModel.toChildArray();
		for (IModelElement childElement : childElements) {
			if (childElement instanceof IClass) {
				extractClass((IClass) childElement, uPackage);
			} else if (childElement instanceof INARY) {
				extractNary((INARY) childElement, uPackage);
			} else if (childElement instanceof IPackage) {
				extractPackagedPackage((IPackage) childElement, uPackage);
			}
		}
		parent.getSubPackages().add(uPackage);
		exportedPackages.add(uPackage);
	}

	private void extractAttributes(IClass classModel, UClass uClass) {
        var attributeIter = classModel.attributeIterator();
		while (attributeIter.hasNext()) {
			IAttribute attribute = (IAttribute) attributeIter.next();
			AttributeData attr = new AttributeData(attribute.getVisibility(), attribute.getName(),
					attribute.getTypeAsString(), attribute.getInitialValueAsString(), attribute.getScope());

			uClass.addAttribute(attr);
		}

        var literalIter = classModel.enumerationLiteralIterator();
		while (literalIter.hasNext()) {
			ApplicationManager.instance().getViewManager().showMessage("literal being extracted.");
			IEnumerationLiteral literal = (IEnumerationLiteral) literalIter.next();
			AttributeData attr = new AttributeData("", literal.getName(), "", "", "instance");
			uClass.addAttribute(attr);
		}
	}

	private void extractOperations(IClass classModel, UClass uClass) {
        var operationIter = classModel.operationIterator();
		while (operationIter.hasNext()) {
			IOperation operation = (IOperation) operationIter.next();
			OperationData op = new OperationData(operation.getVisibility(), operation.getName(),
					operation.getReturnTypeAsString(), operation.isAbstract(), null, operation.getScope());

            var paramIterator = operation.parameterIterator();
			while (paramIterator.hasNext()) {
				IParameter parameter = (IParameter) paramIterator.next();
				Parameter paramData = new Parameter(parameter.getName(), parameter.getTypeAsString(),
						parameter.getDefaultValueAsString());
				op.addParameter(paramData);
			}
			uClass.addOperation(op);
		}
	}

	public List<UClass> getExportedClasses() {
		return exportedClasses;
	}

	public List<RelationshipData> getRelationshipDatas() {
		return relationshipDatas;
	}

	public List<UPackage> getExportedPackages() {
		return exportedPackages;
	}

	public List<NaryData> getExportedNary() {
		return exportedNary;
	}

	private String formatAlias(String name) {
			// 与 PlantUMLWriter.formatAlias 保持一致：放行所有语言的字母和数字
			return name.replaceAll("[^\\p{L}\\p{N}]", "_");
	}
}
