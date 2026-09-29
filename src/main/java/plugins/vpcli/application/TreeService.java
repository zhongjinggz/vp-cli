package plugins.vpcli.application;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.factory.IModelElementFactory;
import plugins.vpcli.drivenadapter.ProjectManagerFactory;

import java.util.ArrayList;
import java.util.List;

public class TreeService {

	private final ProjectManagerFactory projectManagerFactory;

	public TreeService(ProjectManagerFactory projectManagerFactory) {
		this.projectManagerFactory = projectManagerFactory;
	}

	public void tree() {
		IModelElement[] topLevelElements = projectManagerFactory.getProjectManager().getProject().toModelElementArray();
		List<IModelElement> roots = filterNamespaceContainers(topLevelElements);
		for (IModelElement element : roots) {
			System.out.println(element.getName());
			printNamespaceTree(element, "");
		}
	}

	private void printNamespaceTree(IModelElement element, String prefix) {
		List<IModelElement> children = filterNamespaceContainers(element.toChildArray());
		for (int i = 0; i < children.size(); i++) {
			boolean isLast = (i == children.size() - 1);
			String branch = isLast ? "└── " : "├── ";
			System.out.println(prefix + branch + children.get(i).getName());
			String childPrefix = prefix + (isLast ? "    " : "│   ");
			printNamespaceTree(children.get(i), childPrefix);
		}
	}

	private List<IModelElement> filterNamespaceContainers(IModelElement[] elements) {
		List<IModelElement> result = new ArrayList<>();
		for (IModelElement element : elements) {
			if (isNamespaceContainer(element)) {
				result.add(element);
			}
		}
		return result;
	}

	private boolean isNamespaceContainer(IModelElement element) {
		String type = element.getModelType();
		return IModelElementFactory.MODEL_TYPE_PACKAGE.equals(type)
				|| IModelElementFactory.MODEL_TYPE_MODEL.equals(type);
	}
}
