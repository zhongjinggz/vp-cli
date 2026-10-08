package plugins.vpcli.domain.myuml.mydiagram;

import java.util.*;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.connector.IContainmentUIModel;
import com.vp.plugin.model.*;

import plugins.vpcli.domain.myuml.myclassifier.MyAssociation;
import plugins.vpcli.domain.myuml.myclassifier.AttributeData;
import plugins.vpcli.domain.myuml.myclassifier.MyClass;
import plugins.vpcli.domain.myuml.myclassifier.MyNary;
import plugins.vpcli.domain.myuml.myclassifier.MyOperation;
import plugins.vpcli.domain.myuml.myclassifier.MyOperation.Parameter;
import plugins.vpcli.domain.myuml.mypackage.MyPackage;
import plugins.vpcli.domain.myuml.mycommon.MyRelationship;

import static com.vp.plugin.diagram.IShapeTypeConstants.SHAPE_TYPE_PACKAGE;

public class MyClassDiagram extends MyDiagram {

	private final IDiagramUIModel diagramUIModel;

	private final List<MyClass> exportedClasses = new ArrayList<>();
	private final List<MyRelationship> myRelationships = new ArrayList<>();
	private final List<MyPackage> exportedPackages = new ArrayList<>();
	private final List<MyNary> exportedNary = new ArrayList<>();


    private final List<MyNary> allExportedNary = new ArrayList<>();

	public MyClassDiagram(IDiagramUIModel diagramUIModel) {
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



	private void extractClass(IClass classModel, MyPackage myPackage) {
		boolean isInPackage = !isRootLevelInDiagram(classModel);
		MyClass myClass = new MyClass(classModel.getName()
			, classModel.isAbstract()
			, classModel.getVisibility()
			, isInPackage);
		myClass.setDescription(classModel.getDescription());
		myClass.setStereotypes(extractStereotypes(classModel));
		extractAttributes(classModel, myClass);
		extractOperations(classModel, myClass);
		
		exportedClasses.add(myClass);
		if (myPackage != null)
			myPackage.getClasses().add(myClass);
	}

	private void extractNary(INARY naryModel, MyPackage uPackage) {
		boolean isInPackage = !isRootLevelInDiagram(naryModel);
		String name = naryModel.getName();
		String id = naryModel.getId();
		MyNary myNary = new MyNary(name, id, isInPackage);
		myNary.setDescription(naryModel.getDescription());

		if (uPackage != null)
			uPackage.getNaries().add(myNary);
		else exportedNary.add(myNary); // I changed if bug

		allExportedNary.add(myNary); // Naries are to be reversed by id so whether in package or not, need to add so that relationships aren't pointing to null.
	}


	private String getNaryAliasById(String naryId) {
		for (MyNary myNary : allExportedNary) {
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
		MyRelationship myRelationship = new MyRelationship(sourceName, targetName, "Containment", "");
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
			MyAssociation myAssociation = new MyAssociation(sourceName, targetName, relationship.getModelType(),
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

		MyRelationship myRelationship = new MyRelationship(sourceName, targetName, relationship.getModelType(), relationship.getName());
		myRelationships.add(myRelationship);
	}

	private void extractPackage(IPackage packageModel) {

		if (isRootLevelInDiagram(packageModel)) {
			MyPackage uPackage = new MyPackage(packageModel.getName(), null, null, null, false, false);
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

	private void extractPackagedPackage(IPackage packageModel, MyPackage parent) {

		MyPackage uPackage = new MyPackage(packageModel.getName(), null, null, null, true, false);
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

	private void extractAttributes(IClass vpClass, MyClass myClass) {
        var attributeIter = vpClass.attributeIterator();
		while (attributeIter.hasNext()) {
			IAttribute attribute = (IAttribute) attributeIter.next();
			AttributeData attr = new AttributeData(attribute.getVisibility(), attribute.getName(),
					attribute.getTypeAsString(), attribute.getInitialValueAsString(), attribute.getScope());

			myClass.addAttribute(attr);
		}

        var literalIter = vpClass.enumerationLiteralIterator();
		while (literalIter.hasNext()) {
			ApplicationManager.instance().getViewManager().showMessage("literal being extracted.");
			IEnumerationLiteral literal = (IEnumerationLiteral) literalIter.next();
			AttributeData attr = new AttributeData("", literal.getName(), "", "", "instance");
			myClass.addAttribute(attr);
		}
	}

	private void extractOperations(IClass classModel, MyClass myClass) {
        var operationIter = classModel.operationIterator();
		while (operationIter.hasNext()) {
			IOperation operation = (IOperation) operationIter.next();
			MyOperation op = new MyOperation(operation.getVisibility(), operation.getName(),
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

	public List<MyClass> getExportedClasses() {
		return exportedClasses;
	}

	public List<MyRelationship> getRelationshipDatas() {
		return myRelationships;
	}

	public List<MyPackage> getExportedPackages() {
		return exportedPackages;
	}

	public List<MyNary> getExportedNary() {
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
