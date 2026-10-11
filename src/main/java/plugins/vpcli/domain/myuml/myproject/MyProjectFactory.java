package plugins.vpcli.domain.myuml.myproject;

import com.vp.plugin.model.IProject;
import plugins.vpcli.drivenadapter.VPProjectRepository;

// Singleton
// 由于MyProjectFactory 被依赖注入机制保证了全局唯一，因此不用传统的基于静态属性的单例机制
public class MyProjectFactory {
    private final VPProjectRepository vpProjectRepository;

    public MyProjectFactory(VPProjectRepository vpProjectRepository) {
        this.vpProjectRepository = vpProjectRepository;
    }

    private MyProject myProject;

    public MyProject fromGlobal() {
        if (myProject == null) {
            IProject vpProject = vpProjectRepository.fromVisualParadigm();
            myProject = new MyProject(
                vpProject.getId(),
                vpProject.getName());
        }
        return myProject;
    }
}
