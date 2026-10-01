package plugins.vpcli.application;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.factory.IModelElementFactory;
import plugins.vpcli.domain.myuml.upackage.UPackage;
import plugins.vpcli.drivenadapter.ProjectRepository;

import java.util.ArrayList;
import java.util.List;

public class TreeService {

	private final ProjectRepository projectRepository;

	public TreeService(ProjectRepository projectRepository) {
		this.projectRepository = projectRepository;
	}

	public void tree() {
		var topLevelVPElements = projectRepository.getProject().toModelElementArray();
		var packages = toUPackages(topLevelVPElements);
		printPackageTree(packages);
	}

	private void printPackageTree(List<UPackage> packages) {
		for (UPackage aPackage : packages) {
			System.out.println(aPackage.getName());
			printSubPackages(aPackage, "");
		}
	}

	private List<UPackage> toUPackages(IModelElement[] vpElements) {
		List<UPackage> result = new ArrayList<>();
        for ( var aVPElement : vpElements ) {
			if (isPackage(aVPElement)) {
				var aPackage = toUPackage(aVPElement);
				var subPackages = toUPackages(aVPElement.toChildArray());
				aPackage.addChildren(subPackages);
				result.add(aPackage);
			}
		}
		return result;
    }

    private UPackage toUPackage(IModelElement vpElement) {
        return new UPackage(vpElement.getId(), vpElement.getName());
    }


	private boolean isPackage(IModelElement element) {
		String type = element.getModelType();
		return IModelElementFactory.MODEL_TYPE_PACKAGE.equals(type)
				|| IModelElementFactory.MODEL_TYPE_MODEL.equals(type);
	}

	private void printSubPackages(UPackage aPackage, String prefix) {
		var subPackages = aPackage.getSubPackages();
		int i = 0;
		int size = subPackages.size();
		for (var subPackage : subPackages) {
			boolean isLast = (i == size - 1);
			String branch = isLast ? "└── " : "├── ";
			System.out.println(prefix + branch + subPackage.getName());
			String subPrefix = prefix + (isLast ? "    " : "│   ");
			printSubPackages(subPackage, subPrefix);
			i++;
		}
	}
}
