package plugins.vpcli.domain.myuml.mydiagram1;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.*;
import plugins.vpcli.domain.myuml.myactivity.MyForkJoin;
import plugins.vpcli.domain.myuml.mycommon.MyRelationship;
import plugins.vpcli.domain.myuml.mystatemachine.MyHistory;
import plugins.vpcli.domain.myuml.mystatemachine.MyStateChoice;
import plugins.vpcli.domain.myuml.mystatemachine.MyState;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.vp.plugin.diagram.IShapeTypeConstants.*;

public class MyStateDiagram extends MyDiagram {

    private final IDiagramUIModel diagram;
    private final List<MyState> myStates = new ArrayList<>();
    private final List<MyHistory> histories = new ArrayList<>();
    private List<MyRelationship> transitions = new ArrayList<>();
    private final List<MyStateChoice> choices = new ArrayList<>();
    private final List<MyForkJoin> forkJoins = new ArrayList<>();

    // call at the very end to fix insane aliases
    private void prettifyAliases(List<? extends MyState> stateDatas, String type) {
            // iterate through them and change the aliases
        int alias_counter = 0;
        for (MyState myState : stateDatas) {
            myState.setAlias(type + "_" + alias_counter);
            alias_counter++;
        }
    }


    public MyStateDiagram(IDiagramUIModel diagram) {
        this.diagram = diagram;
    }

    @Override
    public void extract() {
        IDiagramElement[] allElements = diagram.toDiagramElementArray();

        List<IRelationship> deferredRelationships = new ArrayList<>();

        IDiagramElement[] stateDiagramElems = diagram.toDiagramElementArray(SHAPE_TYPE_REGION);

        for (IDiagramElement stateElement : stateDiagramElems) {
            String packageModelId = stateElement.getModelElement().getId();
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

            if (modelElement instanceof IState2) {
               if (isRootLevelInDiagram(modelElement)) {
                   extractState((IState2) modelElement, null);
               }
            } else if (modelElement instanceof IInitialPseudoState) {
                if (isRootLevelInDiagram(modelElement)) {
                    extractInitFin(modelElement, null, true);
                }
            } else if (modelElement instanceof IFinalState2) {
                if (isRootLevelInDiagram(modelElement)) {
                    extractInitFin(modelElement, null, false);
                }
            } else if (modelElement instanceof IChoice) {
                if (isRootLevelInDiagram(modelElement)) {
                    extractChoice((IChoice) modelElement);
                }
            } else if (modelElement instanceof IShallowHistory || modelElement instanceof IDeepHistory) {
                if (isRootLevelInDiagram(modelElement)) {
                    extractHistory(modelElement, null);
                }
            } else if (modelElement instanceof  IFork || modelElement instanceof IJoin) {
                if (isRootLevelInDiagram(modelElement)) {
                    extractForkJoin(modelElement, null);
                }
            } else if (modelElement instanceof INOTE) {
                extractNote((INOTE) modelElement);
            } else if (modelElement instanceof IRelationship) {
                deferredRelationships.add((IRelationship) modelElement); // Defer relationships
            } else {
                allExportedElements.remove(modelElement);
                if (!(modelElement instanceof IRegion)) {
                    ApplicationManager.instance().getViewManager()
                            .showMessage("Warning: diagram element " + modelElement.getName()
                                    + " is of unsupported type " + modelElement.getModelType() + " and will not be processed ... ");
                    addWarning("Diagram element " + modelElement.getName()
                            + " is of unsupported type " + modelElement.getModelType() + " and was not processed. ");
                }
            }
        }

        prettifyAliases(myStates, "state");
        prettifyAliases(choices, "choice");
        prettifyAliases(forkJoins, "forkjoin");
        prettifyAliases(histories, "history");

        for (IRelationship relationship : deferredRelationships) {
            extractTransition(relationship);
        }
        filterAndExportTransitions();
    }

    private void extractHistory(IModelElement modelElement, MyState.StateRegion regionData) {
        MyHistory history = new MyHistory(modelElement.getName());
        history.setDeep(modelElement instanceof IDeepHistory);
        history.setId(modelElement.getId());
        history.setInState(modelElement.getParent() instanceof IRegion || regionData != null);
        if (regionData != null) {
            regionData.getSubStates().add(history);
        }
        histories.add(history);
    }

    private void extractForkJoin(IModelElement modelElement, MyState.StateRegion regionData) {
        String id = modelElement.getId();

        MyForkJoin forkJoin = new MyForkJoin(modelElement.getName());
        forkJoin.setFork(modelElement instanceof IFork);
        forkJoin.setInState(modelElement.getParent() instanceof IRegion || regionData != null);
        forkJoin.setId(id);

        if (regionData != null) {
            regionData.getSubStates().add(forkJoin);
        }
        forkJoins.add(forkJoin);
    }

    private void extractInitFin(IModelElement initElement, MyState.StateRegion parentRegion, boolean isStart) {
        boolean isInState = (initElement.getParent() instanceof IRegion);

        MyState myState = new MyState(initElement.getName());
        myState.setDescription(initElement.getDescription());
        myState.setInState(isInState);
        myState.setStart(isStart);
        myState.setEnd(!isStart);
        myState.setId(initElement.getId());

        if (parentRegion != null) {
            parentRegion.getSubStates().add(myState);
        }
        myStates.add(myState);
    }

    private void extractTransition(IRelationship relationship) {
        IModelElement source = relationship.getFrom();
        IModelElement target = relationship.getTo();
        String sourceName;
        String targetName;
        try {
            sourceName = source.getName();
            targetName = target.getName();
        }
        catch (NullPointerException e) {
            ApplicationManager.instance().getViewManager()
                    .showMessage("Warning: One of the relationship's elements were null possibly due to a previously imported illegal relationship (e.g. an Anchor between classes)");
            addWarning("One of the relationship's elements were null possibly due to a previously imported illegal relationship (e.g. an Anchor between classes)");
            return;
        }

        sourceName = getAliasByType(source, sourceName);
        targetName = getAliasByType(target, targetName);

        if (sourceName == null || targetName == null) {
            ApplicationManager.instance().getViewManager()
                    .showMessage("Warning: One of the relationship's elements were null possibly due to illegal relationship (e.g. an Anchor between classes)");

            addWarning("One of the relationship's elements were null possibly due to a previously imported illegal relationship (e.g. an Anchor between classes)");
            return;
        }

        if (Objects.equals(relationship.getModelType(), "Anchor")) return;
        MyRelationship myRelationship = new MyRelationship(sourceName, targetName, relationship.getModelType(), relationship.getName());

        MyState sourceState = findStateById(source.getId());
        MyState targetState = findStateById(target.getId());
        for (MyState state : myStates) {
            for (MyState.StateRegion region : state.getRegions()) {
                if (region.getSubStates().contains(sourceState) && region.getSubStates().contains(targetState)) {
                    region.getRegTransitions().add(myRelationship);
                    return;
                }
            }
        }

        transitions.add(myRelationship);

    }
    private String getAliasByType(IModelElement element, String original) {
        if (element instanceof IState2 || element instanceof IInitialPseudoState ||
                element instanceof IFinalState2 || element instanceof IChoice ||
                element instanceof IDeepHistory || element instanceof IShallowHistory ||
                element instanceof IFork || element instanceof IJoin) {
            return getStateAliasById(element.getId());
        } else if (element instanceof INOTE) {
            return getNoteAliasById(element.getId());
        }
        return original;
    }

    private MyState findStateById(String id) {
        for (MyState myState : myStates) {
            if (myState.getId().equals(id)) {
                return myState;
            }
        }
        return null;
    }

    private String getStateAliasById(String id) {
        for (MyState myState : myStates) {
            if (myState.getId().equals(id)) {
                return myState.getAlias();
            }
        }

        for (MyStateChoice stateChoice : choices) {
            if (stateChoice.getId().equals(id)) {
                return stateChoice.getAlias();
            }
        }

        for (MyState hist : histories) {
            if (hist.getId().equals(id)) {
                return hist.getAlias();
            }
        }

        for (MyForkJoin forkJoin : forkJoins) {
            if (forkJoin.getId().equals(id)) {
                return forkJoin.getAlias();
            }
        }
        return null;
    }

    private void extractChoice(IChoice choiceModel) {
        boolean isInState = (choiceModel.getParent() instanceof IRegion);

        String id = choiceModel.getId();

        MyStateChoice stateChoice = new MyStateChoice(choiceModel.getName());
        stateChoice.setDescription(choiceModel.getDescription());
        stateChoice.setInState(isInState);
        stateChoice.setId(id);

        choices.add(stateChoice);

    }

    private void extractState(IState2 stateModel, MyState.StateRegion parentRegion) {

        boolean isInState = (stateModel.getParent() instanceof IRegion);

        String id = stateModel.getId();

        MyState myState = new MyState(stateModel.getName());
        myState.setDescription(stateModel.getDescription());
        myState.setInState(isInState);
        myState.setId(id);

        var regionIter = stateModel.regionIterator();
        while (regionIter.hasNext()) {
            IRegion regionModel = (IRegion) regionIter.next();
            MyState.StateRegion regionData  = new MyState.StateRegion();
            myState.getRegions().add(regionData);

            var stateIter = regionModel.state2Iterator();
            while (stateIter.hasNext()) {
                IState2 subStateModel = (IState2) stateIter.next();
                extractState(subStateModel, regionData);
            }

            var initIter = regionModel.initialPseudoStateIterator();
            while (initIter.hasNext()) {
                IInitialPseudoState subInit = (IInitialPseudoState) initIter.next();
                extractInitFin(subInit, regionData, true);
            }

            var finIter = regionModel.finalState2Iterator();
            while (finIter.hasNext()) {
                IFinalState2 subFin = (IFinalState2) finIter.next();
                extractInitFin(subFin, regionData, false);
            }

            var choiceIter = regionModel.choiceIterator();
            while (choiceIter.hasNext()) {
                IChoice subChoice = (IChoice) choiceIter.next();
                extractChoice(subChoice);
            }

            var histIter = regionModel.deepHistoryIterator();
            while (histIter.hasNext()) {
                IDeepHistory hist = (IDeepHistory) histIter.next();
                extractHistory(hist, regionData);
            }

            var histIter2 = regionModel.shallowHistoryIterator();
            while (histIter2.hasNext()) {
                IDeepHistory hist = (IDeepHistory) histIter2.next();
                extractHistory(hist, regionData);
            }

            var forkIter = regionModel.forkIterator();
            while (forkIter.hasNext()) {
                IFork fork = (IFork) forkIter.next();
                extractForkJoin(fork, regionData);
            }

            var joinIter = regionModel.joinIterator();
            while (joinIter.hasNext()) {
                IJoin join = (IJoin) joinIter.next();
                extractForkJoin(join, regionData);
            }

        }

        if (parentRegion != null) {
            parentRegion.getSubStates().add(myState);
        }
        myStates.add(myState);
    }

    private void filterAndExportTransitions() {
        List<MyRelationship> validTransitions = new ArrayList<>();

        for (MyRelationship transition : transitions) {
            MyState sourceState = findStateByAlias(transition.getSource());
            MyState targetState = findStateByAlias(transition.getTarget());

            if (sourceState == null || targetState == null) {
                validTransitions.add(transition);
                continue;
            }

            // Get regions for both states
            MyState.StateRegion sourceRegion = findRegionContainingState(sourceState);
            MyState.StateRegion targetRegion = findRegionContainingState(targetState);

            if (sourceRegion == targetRegion) {
                // Valid if both are in the same region
                validTransitions.add(transition);
            } else if (sourceRegion == null || targetRegion == null) {
                // Valid if one state is not in a region and the other belongs to a single-region state
                MyState containingState = findStateContainingRegion(sourceRegion != null ? sourceRegion : targetRegion);
                if (containingState == null || containingState.getRegions().size() == 1) {
                    validTransitions.add(transition);
                } else {
                    outputLostTransitionWarning(transition, "One state is not in a region, but the other belongs to a multi-region state.");
                }
            } else {
                // Check if both regions belong to a single-region state
                MyState sourceContainingState = findStateContainingRegion(sourceRegion);
                MyState targetContainingState = findStateContainingRegion(targetRegion);

                if (sourceContainingState != null && sourceContainingState == targetContainingState
                        && sourceContainingState.getRegions().size() == 1) {
                    validTransitions.add(transition);
                } else {
                    outputLostTransitionWarning(transition, "States are in different regions of a multi-region state.");
                }
            }
        }

        // Replace original transitions with valid ones
        transitions = validTransitions;
    }

    private void outputLostTransitionWarning(MyRelationship transition, String reason) {
        ApplicationManager.instance().getViewManager()
                .showMessage("Warning: Transition '" + transition.getName() + "' lost during export because it's illegal in PlantUML. Reason: " + reason);
        addWarning("Transition '" + transition.getName() + "' lost during export because it's illegal in PlantUML. Reason: " + reason);
    }


    private MyState.StateRegion findRegionContainingState(MyState state) {
        for (MyState containerState : myStates) {
            for (MyState.StateRegion region : containerState.getRegions()) {
                if (region.getSubStates().contains(state)) {
                    return region;
                }
            }
        }
        return null;
    }

    private MyState findStateContainingRegion(MyState.StateRegion region) {
        for (MyState state : myStates) {
            if (state.getRegions().contains(region)) {
                return state;
            }
        }
        return null;
    }

    private MyState findStateByAlias(String alias) {
        for (MyState myState : myStates) {
            if (myState.getAlias().equals(alias)) {
                return myState;
            }
        }
        return null;
    }

    public List<MyHistory> getHistories() {
        return histories;
    }

    public List<MyState> getStateDatas() {
        return myStates;
    }

    public List<MyForkJoin> getForkJoins() {
        return forkJoins;
    }

    public List<MyRelationship> getTransitions() {
        return transitions;
    }

    public List<MyStateChoice> getChoices() {
        return choices;
    }

}

