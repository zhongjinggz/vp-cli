package plugins.vpcli;

import com.vp.plugin.*;
import plugins.vpcli.application.ListDiagramsService;
import plugins.vpcli.domain.myuml.myproject.TreeConverter;
import plugins.vpcli.domain.myuml.myproject.TreeVisitorFactory;
import plugins.vpcli.domain.vpstruct.VPStructElementFactory;
import plugins.vpcli.domain.vpstruct.VPVisitorFactory;
import plugins.vpcli.drivenadapter.FileFactory;
import plugins.vpcli.drivenadapter.ProjectRepository;
import plugins.vpcli.drivenadapter.TreeDirMaker;
import plugins.vpcli.drivenadapter.TreePrinter;
import plugins.vpcli.drivingadapter.CLIController;
import plugins.vpcli.application.ExportService;
import plugins.vpcli.application.TreeService;
import plugins.vpcli.domain.mydiagram.MyDiagramFactory;
import plugins.vpcli.application.writers.WriterFactory;


public class VPCLI implements VPPlugin, VPPluginCommandLineSupport {
    // Driven Adapters
    private MyDiagramFactory myDiagramFactory;
    private WriterFactory writerFactory;
    private ProjectRepository projectRepository;
    private FileFactory fileFactory;
    private TreePrinter treePrinter;
    private TreeDirMaker treeDirMaker;

    private VPStructElementFactory vpStructElementFactory;
    private TreeVisitorFactory treeVisitorFactory;

    // Domain Services
    private TreeConverter treeConverter;

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
        this.myDiagramFactory = new MyDiagramFactory();
        this.writerFactory = new WriterFactory();
        this.projectRepository = new ProjectRepository();
        this.fileFactory = new FileFactory();
        this.treePrinter = new TreePrinter();
        this.treeDirMaker = new TreeDirMaker();

        this.vpStructElementFactory = new VPStructElementFactory();
        this.treeVisitorFactory = new TreeVisitorFactory();

        // Domain Services
        this.treeConverter = new TreeConverter();

        // Application Services
        this.exportService = new ExportService(
            this.treeConverter
            , this.projectRepository
            , this.myDiagramFactory
            , this.writerFactory
            , this.fileFactory
            , this.treeDirMaker);

        this.treeService = new TreeService(
            this.projectRepository
            , this.treeConverter
            , this.treePrinter
            , this.vpStructElementFactory
            , this.treeVisitorFactory);

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
