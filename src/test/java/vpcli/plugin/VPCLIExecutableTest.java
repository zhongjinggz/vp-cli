package vpcli.plugin;

import com.vp.plugin.VPPluginInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 锁定 {@link VPCLI#makeScriptsExecutable} 行为：插件加载时自动给 script/
 * 下的 vp-* 脚本赋可执行权限，省去用户手动 chmod +x。
 */
public class VPCLIExecutableTest {

    @TempDir
    Path tempDir;

    private final VPCLI plugin = new VPCLI();

    private VPPluginInfo pluginInfoWithScriptDir(File scriptDir) {
        VPPluginInfo info = mock(VPPluginInfo.class);
        when(info.getPluginDir()).thenReturn(scriptDir.getParentFile());
        return info;
    }

    @Test
    void makesVpScriptsExecutable() throws IOException {
        File scriptDir = new File(tempDir.toFile(), "script");
        assertTrue(scriptDir.mkdirs());
        File vpExport = new File(scriptDir, "vp-export");
        File vpTree = new File(scriptDir, "vp-tree");
        File other = new File(scriptDir, "readme.txt");
        for (File f : new File[]{vpExport, vpTree, other}) {
            assertTrue(f.createNewFile());
        }
        // 故意移除可执行位，模拟刚解压/安装后的状态
        assertTrue(vpExport.setExecutable(false));
        assertTrue(vpTree.setExecutable(false));

        plugin.makeScriptsExecutable(pluginInfoWithScriptDir(scriptDir));

        assertTrue(Files.isExecutable(vpExport.toPath()), "vp-* 脚本应被赋可执行权限");
        assertTrue(Files.isExecutable(vpTree.toPath()), "vp-* 脚本应被赋可执行权限");
    }

    @Test
    void isIdempotent_whenAlreadyExecutable() throws IOException {
        File scriptDir = new File(tempDir.toFile(), "script");
        assertTrue(scriptDir.mkdirs());
        File vpExport = new File(scriptDir, "vp-export");
        assertTrue(vpExport.createNewFile());
        assertTrue(vpExport.setExecutable(true));

        assertDoesNotThrow(() -> plugin.makeScriptsExecutable(pluginInfoWithScriptDir(scriptDir)));
        assertTrue(Files.isExecutable(vpExport.toPath()));
    }

    @Test
    void toleratesNullPluginInfo() {
        assertDoesNotThrow(() -> plugin.makeScriptsExecutable(null));
    }

    @Test
    void toleratesMissingScriptDir() throws IOException {
        File parent = new File(tempDir.toFile(), "plugin-without-script");
        assertTrue(parent.mkdirs());
        VPPluginInfo info = mock(VPPluginInfo.class);
        when(info.getPluginDir()).thenReturn(parent);

        assertDoesNotThrow(() -> plugin.makeScriptsExecutable(info));
    }
}
