package plugins.vpcli;

import com.vp.plugin.*;
import plugins.vpcli.application.ListDiagramsService;
import plugins.vpcli.domain.myuml.myproject.ExportDiagramVisitorFactory;
import plugins.vpcli.domain.myuml.myproject.MyProjectFactory;
import plugins.vpcli.domain.myuml.myproject.TreeElementVisitorFactory;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.structure.ViProjectFactory;
import plugins.vpcli.drivenadapter.*;
import plugins.vpcli.drivingadapter.CLIController;
import plugins.vpcli.application.ExportService;
import plugins.vpcli.application.TreeService;
import plugins.vpcli.domain.myuml.mydiagram1.MyDiagramFactory;
import plugins.vpcli.application.writers.WriterFactory;

import java.io.File;
import java.io.IOException;


public class VPCLI implements VPPlugin, VPPluginCommandLineSupport {
    private static final org.slf4j.Logger LOG =
        org.slf4j.LoggerFactory.getLogger(VPCLI.class);

    // Util
    private DirMaker dirMaker;


    // Factories
    private MyDiagramFactory myDiagramFactory;
    private WriterFactory writerFactory;
    private FileFactory fileFactory;
    //private TreePrinter treePrinter;
    private TreeDirMaker treeDirMaker;

    private ViProjectFactory viProjectFactory;
    private TreeElementVisitorFactory treeVisitorFactory;

    // Repositories
    private VPProjectRepository vpProjectRepository;


    // Domain Services

    // Application Services
    private ExportService exportService;
    private TreeService treeService;
    private ListDiagramsService listDiagramsService;

    // Driving Adapter
    private CLIController cliController;
    private ExportDiagramVisitorFactory exportVisitorFactory;
    private MyProjectFactory myProjectFactory;


    public VPCLI() {
        injectDependencies();
    }

    // TODO 优化依赖注入，按功能分开不同的控制器，只注入本次功能需要的依赖
    void injectDependencies() {
        // Util
        this.dirMaker = new DirMaker();
        // Repository
        this.vpProjectRepository = new VPProjectRepository();

        // Factory
        this.fileFactory = new FileFactory();
        this.myDiagramFactory = new MyDiagramFactory();
        this.writerFactory = new WriterFactory(this.fileFactory);
        this.treeDirMaker = new TreeDirMaker();
        this.myProjectFactory = new MyProjectFactory();

        this.viProjectFactory = new ViProjectFactory();
        this.treeVisitorFactory = new TreeElementVisitorFactory();
        this.exportVisitorFactory = new ExportDiagramVisitorFactory(
            this.dirMaker,
            this.myDiagramFactory,
            this.writerFactory,
            this.myProjectFactory
        );

        // Domain Services

        // Application Services
        this.exportService = new ExportService(
            this.vpProjectRepository,
            this.viProjectFactory,
            this.exportVisitorFactory,
            this.myProjectFactory
        );

        this.treeService = new TreeService(
            this.vpProjectRepository
            , this.viProjectFactory
            , this.treeVisitorFactory);

        this.listDiagramsService = new ListDiagramsService(this.vpProjectRepository);

        // Controller
        this.cliController = new CLIController(
            this.exportService
            , this.treeService
            , this.listDiagramsService);
    }

    @Override
    public void loaded(VPPluginInfo pluginInfo) {
        configureLogging(pluginInfo);
    }

    // 在插件加载阶段按 profile 选择日志输出目标，供 logback.xml 读取。
    // 未指定 -Dvpcli.log.profile 时默认 dev（输出到控制台）。
    private void configureLogging(VPPluginInfo pluginInfo) {
        String profile = System.getProperty("vpcli.log.profile", "dev");
        boolean prod = "prod".equalsIgnoreCase(profile);
        System.setProperty("vpcli.log.appender", prod ? "FILE" : "CONSOLE");

        if (pluginInfo != null) {
            File pluginDir = pluginInfo.getPluginDir();
            if (pluginDir != null) {
                File logDir = new File(pluginDir, "log");
                if (logDir.mkdirs() || logDir.isDirectory()) {
                    System.setProperty("vpcli.log.dir", logDir.getAbsolutePath());
                }
            }
        }
        LOG.info("vpcli.log.profile={}, vpcli.log.dir={}",
            profile, System.getProperty("vpcli.log.dir"));
    }

    @Override
    public void unloaded() {
    }

    // CLI 入口：解析参数并分发到导入/导出逻辑
    @Override
    public void invoke(String[] args) {
        try {
            cliController.invoke(args);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
