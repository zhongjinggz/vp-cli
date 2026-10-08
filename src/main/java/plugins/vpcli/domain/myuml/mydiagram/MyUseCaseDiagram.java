package plugins.vpcli.domain.myuml.mydiagram;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IActor;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.INOTE;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.IRelationship;
import com.vp.plugin.model.ISystem;
import com.vp.plugin.model.IUseCase;

import plugins.vpcli.domain.myuml.myusecase.MyActor;
import plugins.vpcli.domain.myuml.myclassifier.MyAssociation;
import plugins.vpcli.domain.myuml.mypackage.MyPackage;
import plugins.vpcli.domain.myuml.mycommon.MyRelationship;
import plugins.vpcli.domain.myuml.myusecase.MyUseCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.vp.plugin.diagram.IShapeTypeConstants.*;


public class MyUseCaseDiagram extends MyDiagram {

    private final IDiagramUIModel diagram;

    public MyUseCaseDiagram(IDiagramUIModel diagram) {
        this.diagram = diagram;
    }
    
    private final List<MyActor> exportedActors = new ArrayList<>();
    
    private final List<MyRelationship> myRelationships = new ArrayList<>();
    private final List<MyPackage> exportedPackages = new ArrayList<>();
    private final List<MyUseCase> exportedUseCases = new ArrayList<>();

    public void extract() {


        List<IRelationship> deferredRelationships = new ArrayList<>();
        IDiagramElement[] allElements = diagram.toDiagramElementArray();


        IDiagramElement[] packageDiagramElems = diagram.toDiagramElementArray(SHAPE_TYPE_PACKAGE);
        IDiagramElement[] systemDiagramElems = diagram.toDiagramElementArray(SHAPE_TYPE_SYSTEM);

        for (IDiagramElement packageElement : packageDiagramElems) {
            String packageModelId = packageElement.getModelElement().getId();
            packageModelIds.add(packageModelId);
        }
        for (IDiagramElement packageElement : systemDiagramElems) {
            String packageModelId = packageElement.getModelElement().getId();
            packageModelIds.add(packageModelId);
        }



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

            if (modelElement instanceof IActor) {
                extractActor((IActor) modelElement, null);
            } else if (modelElement instanceof IUseCase) {
                extractUseCase((IUseCase) modelElement, null);
            } else if (modelElement instanceof IRelationship) {
                deferredRelationships.add((IRelationship) modelElement);
            } else if (modelElement instanceof IPackage || modelElement instanceof ISystem) {
                extractPackage(modelElement);
            } else if (modelElement instanceof INOTE) {
                this.extractNote((INOTE) modelElement);
            } else {
                allExportedElements.remove(modelElement);
                ApplicationManager.instance().getViewManager().showMessage("Warning: diagram element " + modelElement.getName() +
                        " is UNSUPPORTED and will not be processed ... .");
                addWarning("Diagram element " + modelElement.getName() +
                        " is of unsupported type and was not processed");
            }

        }

        for (IRelationship relationship : deferredRelationships) {
            extractRelationship(relationship);
        }

    }


	private void extractUseCase(IUseCase modelElement, MyPackage uPackage) {
    	boolean isInPackage = !isRootLevelInDiagram(modelElement);
    	String name = modelElement.getName();
    	boolean isBusiness = modelElement.isBusinessModel();
		MyUseCase uUseCase = new MyUseCase(name);
		uUseCase.setInPackage(isInPackage);
        uUseCase.setDescription(modelElement.getDescription());
		uUseCase.setStereotypes(extractStereotypes(modelElement));
		uUseCase.setBusiness(isBusiness);
		exportedUseCases.add(uUseCase);
		if (uPackage != null) uPackage.getUseCases().add(uUseCase);
	}


	private void extractActor(IActor modelElement, MyPackage uPackage) {
		boolean isInPackage = !isRootLevelInDiagram(modelElement);
        boolean isBusiness = modelElement.isBusinessModel();
    	String name = modelElement.getName();
    	MyActor myActor = new MyActor(name);
    	myActor.setInPackage(isInPackage);
        myActor.setDescription(modelElement.getDescription());
    	myActor.setStereotypes(extractStereotypes(modelElement));
        myActor.setBusiness(isBusiness);
    	exportedActors.add(myActor);
    	if (uPackage != null) uPackage.getActors().add(myActor);
	}


    private void extractRelationship(IRelationship relationship) {
        IModelElement source = relationship.getFrom();
        IModelElement target = relationship.getTo();

        if (!allExportedElements.contains(source) || !allExportedElements.contains(target)) {
            return;
        }
        
        String sourceName = source.getName();
        String targetName = target.getName();

        if (source.getName() == null || target.getName() == null) {
            ApplicationManager.instance().getViewManager()
                    .showMessage("Warning: One of the relationship's " +(relationship.getName())+ " elements were null possibly due to illegal relationship (e.g. Anchor between classes) or a hanging connector End");
            addWarning("One of the relationship's elements " + (relationship.getName()) + " were null possibly due to illegal relationship (e.g. Anchor between classes) or a hanging connector End");
        }

        if (source instanceof INOTE) {
            sourceName = getNoteAliasById(source.getId());
        }
        if (target instanceof INOTE) {
            targetName = getNoteAliasById(target.getId());
        }
        
        if (relationship instanceof IAssociation) {
        	IAssociation association = (IAssociation) relationship;
        	
        	IAssociationEnd fromEnd = (IAssociationEnd) association.getFromEnd();
            IAssociationEnd toEnd = (IAssociationEnd) association.getToEnd();



            String fromEndMultiplicity = Objects.toString(fromEnd.getMultiplicity(), "").equals("Unspecified") ? "" : fromEnd.getMultiplicity();
            String toEndMultiplicity = Objects.toString(toEnd.getMultiplicity(), "").equals("Unspecified") ? "" : toEnd.getMultiplicity();


            MyAssociation myAssociation = new MyAssociation(
            		sourceName,
                    targetName,
                    relationship.getModelType(),
                    relationship.getName(),
                    fromEndMultiplicity,
                    toEndMultiplicity,
                    fromEnd.getAggregationKind()
            );

            myRelationships.add(myAssociation);
        	return;
        }

        MyRelationship myRelationship = new MyRelationship(sourceName, targetName, relationship.getModelType(), relationship.getName());
        myRelationships.add(myRelationship);
    }


	private void extractPackage(IModelElement modelElement) {
        
        if (isRootLevelInDiagram(modelElement)) {
	        MyPackage uPackage = new MyPackage(modelElement.getName(), null, null, null, false, modelElement instanceof ISystem);
            uPackage.setDescription(modelElement.getDescription());
            IModelElement[] childElements = modelElement.toChildArray();
	        for (IModelElement childElement : childElements) {
	            if (childElement instanceof IActor) {
	               extractActor((IActor) childElement, uPackage);
	            } else if (childElement instanceof IUseCase) {
	            	extractUseCase((IUseCase) childElement, uPackage);
	            } else if (childElement instanceof IPackage || childElement instanceof ISystem) {
                    extractPackagedPackage(childElement, uPackage);
	                
	            }
	        }
	        exportedPackages.add(uPackage);
        }
    }

	private void extractPackagedPackage(IModelElement childElement, MyPackage parent) {
//        ApplicationManager.instance().getViewManager().showMessage("Extracting package: " + childElement.getName());
        
        MyPackage uPackage = new MyPackage(childElement.getName(), null, null, null, true, childElement instanceof ISystem);
        uPackage.setDescription(childElement.getDescription());
        IModelElement[] childElements = childElement.toChildArray();
        for (IModelElement childElement1 : childElements) {
        	if (childElement1 instanceof IActor) {
	               extractActor((IActor) childElement1, uPackage);
	        } else if (childElement1 instanceof IUseCase) {
	            	extractUseCase((IUseCase) childElement1, uPackage);
            } else if (childElement1 instanceof IPackage || childElement1 instanceof ISystem) {
                extractPackagedPackage(childElement1, uPackage);
            }
        }
        parent.getSubPackages().add(uPackage);
        exportedPackages.add(uPackage);
    }


	public List<MyUseCase> getExportedUseCases() {
		return exportedUseCases;
	}


	public List<MyRelationship> getExportedRelationships() {
		return myRelationships;
	}


	public List<MyPackage> getExportedPackages() {
		return exportedPackages;
	}


	public List<MyActor> getExportedActors() {
		return exportedActors;
	}
	
}
