package plugins.vpcli.application;

import com.vp.plugin.diagram.IDiagramUIModel;
import plugins.vpcli.drivenadapter.VPProjectRepository;

public class ListDiagramsService {
    private final VPProjectRepository vpProjectRepository;
    public ListDiagramsService(VPProjectRepository vpProjectRepository) {
        this.vpProjectRepository = vpProjectRepository;
    }

    public void listDiagrams() {
        IDiagramUIModel[] allDiagrams = vpProjectRepository.fromVisualParadigm().toDiagramArray();
        for (IDiagramUIModel diagram : allDiagrams) {
            System.out.println(diagram.getName() + " | id: " + diagram.getId());
        }
    }
}
