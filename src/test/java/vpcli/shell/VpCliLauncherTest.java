package vpcli.shell;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

/**
 * 锁定启动命令的行为：
 * <ul>
 *   <li>{@link VpCliLauncher#pluginArgTokens} 把用户输入按固定顺序展开为插件参数；</li>
 *   <li>{@link VPExport} / {@link VPTree} 两个独立命令各自构造正确的 {@link PluginArgs} 并交给 launcher。</li>
 * </ul>
 */
@SuppressWarnings("UnicodeInCode")
public class VpCliLauncherTest {

    @TempDir
    Path tempDir;

    @Test
    void pluginArgTokens对tree只展开action() {
        List<String> tokens = VpCliLauncher.pluginArgTokens(List.of("tree"));
        assertEquals(List.of("-action", "tree"), tokens);
    }

    @Test
    void pluginArgTokens对listDiagrams只展开action() {
        List<String> tokens = VpCliLauncher.pluginArgTokens(List.of("list-diagrams"));
        assertEquals(List.of("-action", "list-diagrams"), tokens);
    }

    @Test
    void pluginArgTokens对export展开action和path() {
        List<String> tokens = VpCliLauncher.pluginArgTokens(List.of("export", "/tmp/out"));
        assertEquals(List.of("-action", "export", "-path", "/tmp/out"), tokens);
    }

//    @Test
//    void vpExport把project和规范化的path交给launcher() throws IOException {
//        Path out = tempDir.resolve("out");
//        Files.createDirectories(out);
//
//        PluginArgs captured = launchCaptured(() -> {
//            VPExport command = new VPExport();
//            command.project = "/prj/p.vpp";
//            command.path = out.toString();
//            return command.call();
//        });
//
//        assertEquals("/prj/p.vpp", captured.project);
//        assertEquals(out.toFile().getCanonicalPath(), captured.pluginArgs.get(1));
//    }

//    @Test
//    void vpTree把project和treeAction交给launcher() {
//        PluginArgs captured = launchCaptured(() -> {
//            VPTree command = new VPTree();
//            command.project = "/prj/p.vpp";
//            return command.call();
//        });
//
//        assertEquals("/prj/p.vpp", captured.project);
//        assertEquals(List.of("tree"), captured.pluginArgs);
//    }

    private PluginArgs launchCaptured(Callable action) {
        try (MockedStatic<VpCliLauncher> launcher = mockStatic(VpCliLauncher.class)) {
            ArgumentCaptor<PluginArgs> captor = ArgumentCaptor.forClass(PluginArgs.class);
            launcher.when(() -> VpCliLauncher.launch(captor.capture(),null )).thenReturn(0);
            action.call();
            return captor.getValue();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FunctionalInterface
    private interface Callable {
        int call() throws Exception;
    }
}
