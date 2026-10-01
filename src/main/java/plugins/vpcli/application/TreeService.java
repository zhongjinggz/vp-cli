package plugins.vpcli.application;

import plugins.vpcli.domain.myuml.uproject.UConverter;
import plugins.vpcli.drivenadapter.ProjectRepository;
import plugins.vpcli.drivenadapter.TreePrinter;

public class TreeService {

	private final ProjectRepository projectRepository;
	private final UConverter convert;
	private final TreePrinter treePrinter ;

	public TreeService(ProjectRepository projectRepository
		, UConverter convert
		, TreePrinter treePrinter) {

		this.projectRepository = projectRepository;
		this.convert = convert;
		this.treePrinter = treePrinter;
	}

	public void tree() {
		var topLevelVPElements = projectRepository.getProject().toModelElementArray();
		var packages = convert.fromVPElementsToMyPackages(topLevelVPElements, this);
		treePrinter.printPackageTree(packages);
	}
}
