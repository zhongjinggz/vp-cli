package plugins.vpcli;

import com.vp.plugin.*;
import plugins.vpcli.domain.myuml.myproject.MyConverter;
import plugins.vpcli.drivenadapter.FileFactory;
import plugins.vpcli.drivenadapter.ProjectRepository;
import plugins.vpcli.drivenadapter.TreePrinter;
import plugins.vpcli.drivingadapter.CLIController;
import plugins.vpcli.application.DiagramExportPipeline;
import plugins.vpcli.application.TreeService;
import plugins.vpcli.application.exporter.ExporterFactory;
import plugins.vpcli.application.writers.WriterFactory;

// DOING
// - 将依赖注入作为方法
// - tree: 拆分模型和适配器

// TODO
// - export: fix bug of Class Daigram 包和类同名造成混乱
// - tree: 补测试
// - export：将图表放入目录结构
// - export: 重构生成图表的程序
// - export：将图表放入 markdown
// - export: 图表 markdown 中放入交叉引用
// - export: 根据模型内容生成 markdown
// - export：链接交叉引用
// - 翻译和修改 README
// - 统一处理错误
// - 统一处理日志
// - 清理 IDEA 警告
// - 考虑将 python 改为 java ， 改为多模块项目
// DONE
// - export：按照模型包结构创建目录结构
// - tree: 完成功能
// - 将命令行改为 vp_export
// - 清理 IDEA 警告 - part1
// - 整理成DDD架构
// - 修改菜单位置和菜单名称
// - 修改主类名
// - 改为依赖注入
// - 修改包名

public class VPCLI implements VPPlugin, VPPluginCommandLineSupport {
    // Driven Adapters
    private ExporterFactory exporterFactory;
    private WriterFactory writerFactory;
    private ProjectRepository projectRepository;
    private FileFactory fileFactory;
    private TreePrinter treePrinter;

    // Domain Services
    private MyConverter packageConverter;

    // Application Services
    private DiagramExportPipeline diagramExportPipeline;
    private TreeService treeService;

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

        // Domain Services
        this.packageConverter = new MyConverter();

        // Application Services
        this.diagramExportPipeline = new DiagramExportPipeline(
            projectRepository
            , exporterFactory
            , writerFactory
            , fileFactory);

        this.treeService = new TreeService(
            projectRepository
            , this.packageConverter
            , this.treePrinter);

        // Controller
        this.cliController = new CLIController(
            diagramExportPipeline
            , treeService);
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
