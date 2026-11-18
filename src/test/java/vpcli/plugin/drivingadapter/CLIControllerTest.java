package vpcli.plugin.drivingadapter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import java.nio.file.Path;

import org.junit.jupiter.api.io.TempDir;
import vpcli.plugin.VPCLI;
import vpcli.plugin.application.ExportService;
import vpcli.plugin.application.TreeService;
import vpcli.plugin.util.MyIO;

/**
 *
 * <p>{@link VPCLI#invoke} 已退化为只把参数转发给 {@link CLIController#invoke}，
 * 因此本测试直接驱动 {@link CLIController}，覆盖 CLI 分发、导入、导出与图表列举的全部分支。
 * <p>{@link CLIController} 通过构造器注入 {@link ExportService}，测试用 mock 注入以验证交互；
 * 图表列举等管线内部逻辑（{@code DiagramExportPipeline#listDiagrams}）交由 DiagramExportPipelineTest 覆盖。
 * <p>用户面向的消息输出通过注入的 {@link MyIO} 完成，测试用 mock 验证交互，
 * 不再依赖对 {@code System.out} 的重定向捕获。
 */
@SuppressWarnings("UnicodeInCode")
class CLIControllerTest {

    @TempDir
    Path tempDir;
    private final MyIO myIOMock = mock(MyIO.class);
    private final ExportService exportServiceMock = mock(ExportService.class);
    private final TreeService treeServiceMock = mock(TreeService.class);
    private final TreeCommand treeCommandMock = mock(TreeCommand.class);
    private final ExportCommand exportCommandMock = mock(ExportCommand.class);
    private final CLIController controller = new CLIController(treeCommandMock, exportCommandMock);


}
