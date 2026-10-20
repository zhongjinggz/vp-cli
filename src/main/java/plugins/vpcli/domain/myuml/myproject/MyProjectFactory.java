package plugins.vpcli.domain.myuml.myproject;

import com.vp.plugin.model.IProject;
import org.slf4j.Logger;
import plugins.vpcli.domain.myuml.mycommon.MyElementTypeRepository;
import plugins.vpcli.domain.myuml.mycommon.myelementtype.MyElementTypeProject;
import plugins.vpcli.drivenadapter.VPProjectRepository;

import static org.slf4j.LoggerFactory.getLogger;

// TODO extend MyElementFactory?
// Singleton
// 由于MyProjectFactory 被依赖注入机制保证了全局唯一，因此不用传统的基于静态属性的单例机制
public class MyProjectFactory {
    private final Logger logger;

    private final VPProjectRepository vpProjectRepository;
    private final MyElementTypeProject myElementTypeProject;

    public MyProjectFactory(
        VPProjectRepository vpProjectRepository
        , MyElementTypeProject myElementTypeProject
//        , MyElementTypeRepository myElementTypeRepository
    ) {
        this(vpProjectRepository, myElementTypeProject, getLogger(MyProjectFactory.class));
    }

    // 用于单元测试注入 logger
    public MyProjectFactory(
        VPProjectRepository vpProjectRepository
        , MyElementTypeProject myElementTypeProject
        , Logger logger
    ) {
        super();
        this.vpProjectRepository = vpProjectRepository;
        this.logger = logger;
        this.myElementTypeProject = myElementTypeProject;
    }

    private MyProject myProject;

    public MyProject fromGlobal() {
        if (myProject == null) {
            IProject vpProject = vpProjectRepository.fromVisualParadigm();
            myProject = new MyProject(
                vpProject.getId(),
                vpProject.getName(),
                myElementTypeProject
            );

            logger.debug("Creating MyProject: name=" + vpProject.getName() + " id=" + vpProject.getId());
        }
        return myProject;

    }
}
