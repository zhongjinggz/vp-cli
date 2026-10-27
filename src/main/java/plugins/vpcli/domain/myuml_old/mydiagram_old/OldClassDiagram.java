package plugins.vpcli.domain.myuml_old.mydiagram_old;

import java.util.*;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.connector.IContainmentUIModel;
import com.vp.plugin.model.*;

import plugins.vpcli.domain.myuml_old.myassociation_old.MyAssociation_old;
import plugins.vpcli.domain.myuml_old.myclass_old.MyAttribute_old;
import plugins.vpcli.domain.myuml_old.myclass_old.MyClass_old;
import plugins.vpcli.domain.myuml_old.mynary_old.MyNary_old;
import plugins.vpcli.domain.myuml_old.myclass_old.MyOperation_old;
import plugins.vpcli.domain.myuml_old.myclass_old.MyOperation_old.Parameter;
import plugins.vpcli.domain.myuml_old.mypackage_old.MyPackage_old;
import plugins.vpcli.domain.myuml_old.mycommon_old.MyRelationship_old;

import static com.vp.plugin.diagram.IShapeTypeConstants.SHAPE_TYPE_PACKAGE;

public class OldClassDiagram extends OldDiagram {

	private final IDiagramUIModel diagramUIModel;

	private final List<MyClass_old> exportedClasses = new ArrayList<>();
	private final List<MyRelationship_old> myRelationships = new ArrayList<>();
	private final List<MyPackage_old> exportedPackages = new ArrayList<>();
	private final List<MyNary_old> exportedNary = new ArrayList<>();


    private final List<MyNary_old> allExportedNary = new ArrayList<>();

	public OldClassDiagram(IDiagramUIModel diagramUIModel) {
		this.diagramUIModel = diagramUIModel;
	}

	@Override
	public void extract() {
		IDiagramElement[] allElements = diagramUIModel.toDiagramElementArray();

		IDiagramElement[] packageDiagramElems = diagramUIModel.toDiagramElementArray(SHAPE_TYPE_PACKAGE);
		for (IDiagramElement packageElement : packageDiagramElems) {
			String packageModelId = packageElement.getModelElement().getId();
			packageModelIds.add(packageModelId);
		}


		List<IRelationship> deferredRelationships = new ArrayList<>();

		for (IDiagramElement diagramElement : allElements) {
			IModelElement modelElement = diagramElement.getModelElement();

			if (modelElement == null) {
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



	private void extractClass(IClass classModel, MyPackage_old myPackage) {
		boolean isInPackage = !isRootLevelInDiagram(classModel);
		MyClass_old myClass = new MyClass_old(classModel.getName()
			, classModel.isAbstract()
			, classModel.getVisibility()
			, isInPackage);
		myClass.setDescription(classModel.getDescription());
		myClass.setStereotypes(extractStereotypes(classModel));
		extractAttributes(classModel, myClass);
		extractOperations(classModel, myClass);
		
		exportedClasses.add(myClass);
//		if (myPackage != null)
//			myPackage.getClasses().add(myClass);
	}

	private void extractNary(INARY naryModel, MyPackage_old uPackage) {
		boolean isInPackage = !isRootLevelInDiagram(naryModel);
		String name = naryModel.getName();
		String id = naryModel.getId();
		MyNary_old myNary = new MyNary_old(name, id, isInPackage);
		myNary.setDescription(naryModel.getDescription());

//		if (uPackage != null)
//			uPackage.getNaries().add(myNary);
//		else exportedNary.add(myNary); // I changed if bug

		allExportedNary.add(myNary); // Naries are to be reversed by id so whether in package or not, need to add so that relationships aren't pointing to null.
	}


	private String getNaryAliasById(String naryId) {
		for (MyNary_old myNary : allExportedNary) {
			if (myNary.getId().equals(naryId)) {
				return myNary.getAlias();
			}
		}
		return null;
	}

	private void extractContainment(IContainmentUIModel relationship) {
		IModelElement source = relationship.getFromShape().getModelElement();
		IModelElement target = relationship.getToShape().getModelElement();
		String sourceName = source.getName();
		String targetName = target.getName();
		MyRelationship_old myRelationship = new MyRelationship_old(sourceName, targetName, "Containment", "");
		myRelationships.add(myRelationship);
	}

	private void extractRelationship(IRelationship relationship) {
		IModelElement source = relationship.getFrom();
		IModelElement target = relationship.getTo();
		if (!allExportedElements.contains(source) || !allExportedElements.contains(target)) {
			return;
		}
		if (source.getName() == null || target.getName() == null) {
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
			MyAssociation_old myAssociation = new MyAssociation_old(sourceName, targetName, relationship.getModelType(),
					relationship.getName(), fromEndMultiplicity, toEndMultiplicity, fromEnd.getAggregationKind());
			myRelationships.add(myAssociation);
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

		MyRelationship_old myRelationship = new MyRelationship_old(sourceName, targetName, relationship.getModelType(), relationship.getName());
		myRelationships.add(myRelationship);
	}

	private void extractPackage(IPackage packageModel) {

		if (isRootLevelInDiagram(packageModel)) {
			MyPackage_old uPackage = new MyPackage_old(packageModel.getName(), null, null, null, false, false);
//			uPackage.setDescription(packageModel.getDescription());
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

	private void extractPackagedPackage(IPackage packageModel, MyPackage_old parent) {

		MyPackage_old uPackage = new MyPackage_old(packageModel.getName(), null, null, null, true, false);
//		uPackage.setDescription(packageModel.getDescription());
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

	private void extractAttributes(IClass vpClass, MyClass_old myClass) {
        var attributeIter = vpClass.attributeIterator();
		while (attributeIter.hasNext()) {
			IAttribute attribute = (IAttribute) attributeIter.next();
			MyAttribute_old attr = new MyAttribute_old(attribute.getVisibility(), attribute.getName(),
					attribute.getTypeAsString(), attribute.getInitialValueAsString(), attribute.getScope());

			myClass.addAttribute(attr);
		}

        var literalIter = vpClass.enumerationLiteralIterator();
		while (literalIter.hasNext()) {
			ApplicationManager.instance().getViewManager().showMessage("literal being extracted.");
			IEnumerationLiteral literal = (IEnumerationLiteral) literalIter.next();
			MyAttribute_old attr = new MyAttribute_old("", literal.getName(), "", "", "instance");
			myClass.addAttribute(attr);
		}
	}

	private void extractOperations(IClass classModel, MyClass_old myClass) {
        var operationIter = classModel.operationIterator();
		while (operationIter.hasNext()) {
			IOperation operation = (IOperation) operationIter.next();
			MyOperation_old op = new MyOperation_old(operation.getVisibility(), operation.getName(),
					operation.getReturnTypeAsString(), operation.isAbstract(), null, operation.getScope());

            var paramIterator = operation.parameterIterator();
			while (paramIterator.hasNext()) {
				IParameter parameter = (IParameter) paramIterator.next();
				Parameter paramData = new Parameter(parameter.getName(), parameter.getTypeAsString(),
						parameter.getDefaultValueAsString());
				op.addParameter(paramData);
			}
			myClass.addOperation(op);
		}
	}

	public List<MyClass_old> getExportedClasses() {
		return exportedClasses;
	}

	public List<MyRelationship_old> getRelationshipDatas() {
		return myRelationships;
	}

	public List<MyPackage_old> getExportedPackages() {
		return exportedPackages;
	}

	public List<MyNary_old> getExportedNary() {
		return exportedNary;
	}

	private String formatAlias(String name) {
			// 与 PlantUMLWriter.formatAlias 保持一致：放行所有语言的字母和数字
			return name.replaceAll("[^\\p{L}\\p{N}]", "_");
	}

	public IDiagramUIModel getDiagramUIModel() {
		return diagramUIModel;
	}
}
