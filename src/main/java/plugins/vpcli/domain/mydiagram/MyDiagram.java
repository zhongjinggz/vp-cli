package plugins.vpcli.domain.mydiagram;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.INOTE;
import com.vp.plugin.model.IStereotype;

import plugins.vpcli.domain.myuml.mycommon.MyNote;

public abstract class MyDiagram {

	public abstract void extract();
	private final List<String> warnings = new ArrayList<>();

	private final List<MyNote> myNotes = new ArrayList<>();

	// Set of all exported elements for constant lookup so that no relationships with un-exported elements are written
    protected final Set<IModelElement> allExportedElements = new HashSet<>();
	protected final Set<String> packageModelIds = new HashSet<>();

	protected void extractNote(INOTE noteModel) {
		String name = noteModel.getName();
		String content = noteModel.getDescription();
		String id = noteModel.getId();
		MyNote myNote = new MyNote(name, content, id);
		myNotes.add(myNote);
	}

	protected String getNoteAliasById(String naryId) {
		for (MyNote myNote : myNotes) {
			if (myNote.getId().equals(naryId)) {
				return myNote.getAlias();
			}
		}
		return null;
	}

	protected List<String> extractStereotypes(IModelElement modelElement) {
		List<String> stereotypes = new ArrayList<>();

        var stereoIter = modelElement.stereotypeModelIterator();
		while (stereoIter.hasNext()) {
			IStereotype stereotype = (IStereotype) stereoIter.next();
			String stereotypeString = stereotype.getName();
//			ApplicationManager.instance().getViewManager().showMessage("Stereotype: " + stereotypeString);
			stereotypes.add(stereotypeString);
		}
		return stereotypes;
	}

	protected boolean isRootLevel(IModelElement element) {
		return (element.getParent() == null);
	}

	protected boolean isRootLevelInDiagram(IModelElement modelElement) {
		return isRootLevel(modelElement) || !packageModelIds.contains(modelElement.getParent().getId());
	}

	public List<MyNote> getNotes() {
		return myNotes;
	}

	protected void addWarning(String warning) {
		warnings.add(warning);
	}


}

