package plugins.vpcli;

import com.vp.plugin.*;
import plugins.vpcli.application.ListDiagramsService;
import plugins.vpcli.domain.myuml.myproject.ExportVisitorFactory;
import plugins.vpcli.domain.myuml.myproject.TreeConverter;
import plugins.vpcli.domain.myuml.myproject.TreeVisitorFactory;
import plugins.vpcli.domain.vpstruct.VPStructFactory;
import plugins.vpcli.drivenadapter.FileFactory;
import plugins.vpcli.drivenadapter.FileIO;
import plugins.vpcli.drivenadapter.ProjectRepository;
import plugins.vpcli.drivenadapter.TreeDirMaker;
//import plugins.vpcli.drivenadapter.TreePrinter;
import plugins.vpcli.drivingadapter.CLIController;
import plugins.vpcli.application.ExportService;
import plugins.vpcli.application.TreeService;
import plugins.vpcli.domain.mydiagram.MyDiagramFactory;
import plugins.vpcli.application.writers.WriterFactory;

import java.io.IOException;


public class VPCLI implements VPPlugin, VPPluginCommandLineSupport {
    // Factories
    private MyDiagramFactory myDiagramFactory;
    private WriterFactory writerFactory;
    private FileFactory fileFactory;
    //private TreePrinter treePrinter;
    private TreeDirMaker treeDirMaker;

    private VPStructFactory vpStructFactory;
    private TreeVisitorFactory treeVisitorFactory;

    // Repositories
    private ProjectRepository projectRepository;


    // Domain Services
    private TreeConverter treeConverter;

    // Application Services
    private ExportService exportService;
    private TreeService treeService;
    private ListDiagramsService listDiagramsService;

    // Driving Adapter
    private CLIController cliController;
    private ExportVisitorFactory exportVisitorFactory;
    private FileIO fileIO;


    public VPCLI() {
        injectDependencies();
    }

    // TODO 优化依赖注入，按功能分开不同的控制器，只注入本次功能需要的依赖
    void injectDependencies() {
        // Repository
        this.fileIO = new FileIO();
        this.projectRepository = new ProjectRepository();

        // Factory
        this.myDiagramFactory = new MyDiagramFactory();
        this.writerFactory = new WriterFactory();
        this.fileFactory = new FileFactory();
        this.treeDirMaker = new TreeDirMaker();


        this.vpStructFactory = new VPStructFactory();
        this.treeVisitorFactory = new TreeVisitorFactory();
        this.exportVisitorFactory = new ExportVisitorFactory(
            this.fileIO,
            this.myDiagramFactory,
            this.writerFactory,
            this.fileFactory
        );

        // Domain Services
        this.treeConverter = new TreeConverter();

        // Application Services
        this.exportService = new ExportService(
            this.projectRepository
            , this.myDiagramFactory
            , this.writerFactory
            , this.fileFactory
            , this.vpStructFactory
            , this.exportVisitorFactory
        );

        this.treeService = new TreeService(
            this.projectRepository
            , this.vpStructFactory
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
        try {
            cliController.invoke(args);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
