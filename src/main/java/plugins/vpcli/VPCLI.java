package plugins.vpcli;

import com.vp.plugin.*;
import plugins.vpcli.application.ListDiagramsService;
import plugins.vpcli.domain.myuml.myproject.MyConverter;
import plugins.vpcli.drivenadapter.FileFactory;
import plugins.vpcli.drivenadapter.ProjectRepository;
import plugins.vpcli.drivenadapter.TreeDirMaker;
import plugins.vpcli.drivenadapter.TreePrinter;
import plugins.vpcli.drivingadapter.CLIController;
import plugins.vpcli.application.ExportService;
import plugins.vpcli.application.TreeService;
import plugins.vpcli.application.exporter.ExporterFactory;
import plugins.vpcli.application.writers.WriterFactory;


public class VPCLI implements VPPlugin, VPPluginCommandLineSupport {
    // Driven Adapters
    private ExporterFactory exporterFactory;
    private WriterFactory writerFactory;
    private ProjectRepository projectRepository;
    private FileFactory fileFactory;
    private TreePrinter treePrinter;
    private TreeDirMaker treeDirMaker;

    // Domain Services
    private MyConverter myConverter;

    // Application Services
    private ExportService exportService;
    private TreeService treeService;
    private ListDiagramsService listDiagramsService;

    // Driving Adapter
    private CLIController cliController;


    public VPCLI() {
        injectDependencies();
    }

    // TODO 优化依赖注入，按功能分开不同的控制器，只注入本次功能需要的依赖
    void injectDependencies() {
        // Driven Adapters
        this.exporterFactory = new ExporterFactory();
        this.writerFactory = new WriterFactory();
        this.projectRepository = new ProjectRepository();
        this.fileFactory = new FileFactory();
        this.treePrinter = new TreePrinter();
        this.treeDirMaker = new TreeDirMaker();

        // Domain Services
        this.myConverter = new MyConverter();

        // Application Services
        this.exportService = new ExportService(
            this.myConverter
            , this.projectRepository
            , this.exporterFactory
            , this.writerFactory
            , this.fileFactory
            , this.treeDirMaker);

        this.treeService = new TreeService(
            this.projectRepository
            , this.myConverter
            , this.treePrinter);

        this.listDiagramsService = new ListDiagramsService(this.projectRepository);

        // Controller
        this.cliController = new CLIController(
            this.exportService
            , this.treeService
            , this.listDiagramsService);
    }

    @Override
    public void loaded(VPPluginInfo pluginInfo) {
    }

    @Override
    public void unloaded() {
    }

    // CLI 入口：解析参数并分发到导入/导出逻辑
    @Override
    public void invoke(String[] args) {
        cliController.invoke(args);
    }


}
