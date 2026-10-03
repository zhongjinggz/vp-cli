package plugins.vpcli.application;

import plugins.vpcli.domain.myuml.myproject.TreeConverter;
import plugins.vpcli.drivenadapter.ProjectRepository;
import plugins.vpcli.drivenadapter.TreePrinter;

public class TreeService {

	private final ProjectRepository projectRepository;
	private final TreeConverter convert;
	private final TreePrinter treePrinter ;

	public TreeService(ProjectRepository projectRepository
		, TreeConverter convert
		, TreePrinter treePrinter) {

		this.projectRepository = projectRepository;
		this.convert = convert;
		this.treePrinter = treePrinter;
	}

	public void tree() {
		var topLevelVPElements = projectRepository.getProject().toModelElementArray();
		var packages = convert.fromVPElements(topLevelVPElements);
		treePrinter.print(packages);
	}
}
