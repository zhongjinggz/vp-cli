package plugins.vpcli.application;

import plugins.vpcli.domain.myuml.myproject.MyConverter;
import plugins.vpcli.drivenadapter.ProjectRepository;
import plugins.vpcli.drivenadapter.TreePrinter;

public class TreeService {

	private final ProjectRepository projectRepository;
	private final MyConverter convert;
	private final TreePrinter treePrinter ;

	public TreeService(ProjectRepository projectRepository
		, MyConverter convert
		, TreePrinter treePrinter) {

		this.projectRepository = projectRepository;
		this.convert = convert;
		this.treePrinter = treePrinter;
	}

	public void tree() {
		var topLevelVPElements = projectRepository.getProject().toModelElementArray();
		var packages = convert.fromVPElementsToMyPackages(topLevelVPElements, this);
		treePrinter.print(packages);
	}
}
