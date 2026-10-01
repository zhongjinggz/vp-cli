package plugins.vpcli.application;

import com.vp.plugin.diagram.IDiagramUIModel;
import plugins.vpcli.drivenadapter.ProjectRepository;

public class ListDiagramsService {
    private final ProjectRepository projectRepository;
    public ListDiagramsService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public void listDiagrams() {
        IDiagramUIModel[] allDiagrams = projectRepository.getProject().toDiagramArray();
        for (IDiagramUIModel diagram : allDiagrams) {
            System.out.println(diagram.getName() + " | id: " + diagram.getId());
        }
    }
}
