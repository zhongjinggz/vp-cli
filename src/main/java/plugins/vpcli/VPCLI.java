package plugins.vpcli;

import com.vp.plugin.*;
import plugins.vpcli.application.ListDiagramsService;
import plugins.vpcli.domain.myuml.myproject.ExportDiagramVisitorFactory;
import plugins.vpcli.domain.myuml.myproject.MyProjectFactory;
import plugins.vpcli.domain.myuml.myproject.TreeElementVisitorFactory;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.structure.ViProjectFactory;
import plugins.vpcli.drivenadapter.FileFactory;
import plugins.vpcli.drivenadapter.FileIO;
import plugins.vpcli.drivenadapter.VPProjectRepository;
import plugins.vpcli.drivenadapter.TreeDirMaker;
import plugins.vpcli.drivingadapter.CLIController;
import plugins.vpcli.application.ExportService;
import plugins.vpcli.application.TreeService;
import plugins.vpcli.domain.myuml.mydiagram1.MyDiagramFactory;
import plugins.vpcli.application.writers.WriterFactory;

import java.io.IOException;


public class VPCLI implements VPPlugin, VPPluginCommandLineSupport {
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
    private FileIO fileIO;
    private MyProjectFactory myProjectFactory;


    public VPCLI() {
        injectDependencies();
    }

    // TODO 优化依赖注入，按功能分开不同的控制器，只注入本次功能需要的依赖
    void injectDependencies() {
        // Repository
        this.fileIO = new FileIO();
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
            this.fileIO,
            this.myDiagramFactory,
            this.writerFactory,
            this.fileFactory
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
