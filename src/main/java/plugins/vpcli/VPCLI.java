package plugins.vpcli;

import com.vp.plugin.*;
import plugins.vpcli.application.ListDiagramsService;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagramFactory;
import plugins.vpcli.domain.myuml.myproject.ExportDiagramVisitorFactory;
import plugins.vpcli.domain.myuml.myproject.MyProjectFactory;
import plugins.vpcli.domain.myuml.myproject.TreeElementVisitorFactory;
import plugins.vpcli.domain.outputmodel.MyDiagramToPumlConverter;
import plugins.vpcli.domain.vpvisitor.vpdiagramvisitor.element.MyDiagramElementFactory;
import plugins.vpcli.domain.vpvisitor.vpdiagramvisitor.structure.VPToMyDiagramConverter;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.ViElementFactory;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.structure.ViProjectFactory;
import plugins.vpcli.drivenadapter.*;
import plugins.vpcli.drivingadapter.CLIController;
import plugins.vpcli.application.ExportService;
import plugins.vpcli.application.TreeService;
import plugins.vpcli.domain.myuml.mydiagram_old.OldDiagramFactory;
import plugins.vpcli.application.writers_old.WriterFactory;

import java.io.File;
import java.io.IOException;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import org.slf4j.LoggerFactory;


public class VPCLI implements VPPlugin, VPPluginCommandLineSupport {
    // 必须在 LOG 静态字段之前执行：
    // logback 在 LOG 首次初始化时解析 logback.xml（读到 ${vpcli.log.appender}），
    // 因此必须在此处、早于任何 getLogger()，根据 -Dvpcli.log.profile 设好 appender。
    // vpcli.log.dir 不在此设默认值：loaded() 会用插件目录设置并重载配置，
    // 这里设兜底反而会让 FILE 首次绑定到该目录。
    static {
        boolean prod = "prod".equalsIgnoreCase(System.getProperty("vpcli.log.profile", "dev"));
        System.setProperty("vpcli.log.appender", prod ? "FILE" : "CONSOLE");
    }

    private static final org.slf4j.Logger LOG =
        org.slf4j.LoggerFactory.getLogger(VPCLI.class);

    // Util
    private DirMaker dirMaker;
    private MyConsole myConsole;

    // Factories
    private MyDiagramFactory myDiagramFactory;
    private WriterFactory writerFactory;
    private FileFactory fileFactory;



    //private TreePrinter treePrinter;
    private TreeDirMaker treeDirMaker;

    private ViProjectFactory viProjectFactory;
    private TreeElementVisitorFactory treeVisitorFactory;
    private MyDiagramElementFactory myDiagramElementFactory;

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
    private MyElementFactory myElementFactory;
    private ViElementFactory viElementFactory;
    private VPToMyDiagramConverter vpToMyDiagramConverter;
    private MyDiagramToPumlConverter myDiagramToPumlConverter;


    public VPCLI() {
        injectDependencies();
    }

    // TODO 优化依赖注入，按功能分开不同的控制器，只注入本次功能需要的依赖
    void injectDependencies() {

        // Repository vs Factory
        // Repository: 已有的内容的存取（不含 new）
        // Factory: 从无到有创建（用 new）

        // Util
        this.dirMaker = new DirMaker();
        this.myConsole = new MyConsole();

        // Repository
        this.vpProjectRepository = new VPProjectRepository();

        // Factory
        this.fileFactory = new FileFactory();
        this.myDiagramFactory = new MyDiagramFactory();
        this.writerFactory = new WriterFactory(this.fileFactory);
        this.treeDirMaker = new TreeDirMaker();
        this.myProjectFactory = new MyProjectFactory(vpProjectRepository);
        this.myElementFactory = new MyElementFactory();
        this.viElementFactory = new ViElementFactory(myElementFactory);
        this.myDiagramElementFactory = new MyDiagramElementFactory();

        this.vpToMyDiagramConverter = new VPToMyDiagramConverter(myDiagramFactory, myDiagramElementFactory);
        this.myDiagramToPumlConverter = new MyDiagramToPumlConverter();


        this.viProjectFactory = new ViProjectFactory(viElementFactory);
        this.treeVisitorFactory = new TreeElementVisitorFactory();
        this.exportVisitorFactory = new ExportDiagramVisitorFactory(
            this.dirMaker,
            this.myDiagramFactory,
            this.writerFactory,
            this.myProjectFactory,
            this.vpToMyDiagramConverter,
            this.myDiagramToPumlConverter
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
            , this.listDiagramsService, myConsole);
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
        // FILE appender 的文件路径在 logback 首次解析配置时即被绑定，
        // 之后修改 vpcli.log.dir 系统属性不会让它换路径。这里在拿到插件目录后
        // 重载一次配置，使 FILE appender 指向插件目录下的 log/。
        if (prod) {
            reloadLogbackConfiguration();
        }
        LOG.info("vpcli.log.profile={}, vpcli.log.dir={}",
            profile, System.getProperty("vpcli.log.dir"));
    }

    // 重读 classpath 上的 logback.xml 并重建 appender，使 FILE 采用最新的 vpcli.log.dir。
    private void reloadLogbackConfiguration() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();
        try {
            new ContextInitializer(context).autoConfig();
        } catch (JoranException e) {
            LOG.error("Failed to reload logback configuration", e);
        }
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
