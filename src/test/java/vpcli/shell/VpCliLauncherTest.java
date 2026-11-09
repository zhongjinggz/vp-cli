package vpcli.shell;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 锁定 {@link VpCliLauncher#pluginArgTokens} 行为：把子命令的参数
 * 按旧实现固定顺序 {@code -action -> -path -> -target} 展开为插件命令行参数。
 */
public class VpCliLauncherTest {

    @Test
    void export显式target时生成target参数() throws IOException {
        List<String> tokens = VpCliLauncher.pluginArgTokens(
            List.of("export", "DiagramA", "/tmp/out"));
        assertEquals(List.of("-action", "export", "-target", "DiagramA", "-path", "/tmp/out"), tokens);
    }

    @Test
    void tree只展开action参数() throws IOException {
        List<String> tokens = VpCliLauncher.pluginArgTokens(List.of("tree"));
        assertEquals(List.of("-action", "tree"), tokens);
    }

    @Test
    void listDiagrams只展开action参数() throws IOException {
        List<String> tokens = VpCliLauncher.pluginArgTokens(List.of("list-diagrams"));
        assertEquals(List.of("-action", "list-diagrams"), tokens);
    }
}
