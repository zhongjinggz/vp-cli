package plugins.vpcli.application;

import plugins.vpcli.domain.myuml.myproject.ExportElementVisitorFactory1;
import plugins.vpcli.domain.myuml.myproject.MyProjectFactory;
import plugins.vpcli.domain.myuml.myproject.MyProject;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.structure.ViProjectFactory;
import plugins.vpcli.drivenadapter.VPProjectRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static plugins.vpcli.domain.myuml.mycommon.ElementType.*;

public class ExportService {

    private final VPProjectRepository vpProjectRepository;
    private final ViProjectFactory viProjectFactory;
    private final ExportElementVisitorFactory1 exportVisitorFactory;
    private MyProjectFactory myProjectFactory;

    public ExportService(VPProjectRepository vpProjectRepository,
                         ViProjectFactory viProjectFactory,
                         ExportElementVisitorFactory1 exportVisitorFactory,
                         MyProjectFactory myProjectFactory) {

        this.vpProjectRepository = vpProjectRepository;
        this.viProjectFactory = viProjectFactory;
        this.exportVisitorFactory = exportVisitorFactory;
        this.myProjectFactory = myProjectFactory;
    }

    public void exportAll(File exportLocation) throws IOException {

        var project = vpProjectRepository.getProject();

        exportVisitorFactory.setRootDir(exportLocation);
        var viProject = viProjectFactory.create(project);
        viProject.setElementTypes(List.of(
            PACKAGE
            , MODEL
            , DIAGRAM));


        MyProject myProject = myProjectFactory.get();
        viProject.setMyProject(myProject);

        viProject.accept(this.exportVisitorFactory);
    }

    public void exportSpecificDiagram(String target, File exportLocation) throws IOException {
//        IDiagramUIModel targetDiagram = projectRepository.getProject().getDiagramById(target);
//        this.exportADiagram(targetDiagram, exportLocation);
    }

}