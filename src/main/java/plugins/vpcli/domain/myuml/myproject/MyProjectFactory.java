package plugins.vpcli.domain.myuml.myproject;

import com.vp.plugin.model.IProject;
import org.slf4j.Logger;
import plugins.vpcli.drivenadapter.VPProjectRepository;

import static org.slf4j.LoggerFactory.getLogger;

// TODO extend MyElementFactory?
// Singleton
// 由于MyProjectFactory 被依赖注入机制保证了全局唯一，因此不用传统的基于静态属性的单例机制
public class MyProjectFactory {
    private final Logger logger;

    private final VPProjectRepository vpProjectRepository;

    public MyProjectFactory(
        VPProjectRepository vpProjectRepository
//        , MyElementTypeProject myElementTypeProject
    ) {
        this(vpProjectRepository, getLogger(MyProjectFactory.class));
    }

    // 用于单元测试注入 logger
    public MyProjectFactory(
        VPProjectRepository vpProjectRepository
        , Logger logger
    ) {
        super();
        this.vpProjectRepository = vpProjectRepository;
        this.logger = logger;
    }

    private MyProject myProject;

    public MyProject fromGlobal() {
        if (myProject == null) {
            IProject vpProject = vpProjectRepository.fromVisualParadigm();
            myProject = new MyProject(
                vpProject.getId(),
                vpProject.getName()
            );

            logger.debug("Creating MyProject: name={} id={}", vpProject.getName(), vpProject.getId());
        }
        return myProject;

    }
}
