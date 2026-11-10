package vpcli.plugin;

import com.vp.plugin.VPPluginInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 锁定 {@link VPCLI#buildLoggingConfigForPrd} 产出的配置映射与 {@link VPCLI#resolvePluginLogDir}
 * 行为，保证从 logback 迁移到 Tinylog 时的输出格式、级别、滚动策略与原配置等价。
 * 所有可改参数均从配置文件（tinylog.properties，dev./prod. 前缀）读取，而非硬编码。
 */
public class VPCLIConfigurationTest {

    @TempDir
    Path tempDir;

    private final VPCLI plugin = new VPCLI();

    @AfterEach
    void cleanSystemProps() {
        System.clearProperty("vpcli.log.profile");
        System.clearProperty("vpcli.log.dir");
    }

    private Properties realLoggingProperties() throws IOException {
        try (InputStream in = VPCLI.class.getResourceAsStream("/tinylog.properties")) {
            Properties props = new Properties();
            props.load(in);
            return props;
        }
    }

    @Test
    void devConfig_isConsoleOnly() throws Exception {
        Map<String, String> cfg = plugin.buildLoggingConfigForDev(realLoggingProperties());

        assertEquals("console", cfg.get("writer"));
        assertEquals("trace", cfg.get("writer.level"));
        assertEquals("{date:HH:mm:ss.SSS} {level:min-size=5} {class-name} - {message}",
            cfg.get("writer.format"));
        // dev 不配置 rolling-file 相关属性
        assertNull(cfg.get("writer.file"));
        assertNull(cfg.get("writer.latest"));
        assertNull(cfg.get("writer.policies"));
        assertNull(cfg.get("writer.backups"));
        // 采用 Tinylog 默认流划分：不固定 writer.stream
        assertFalse(cfg.containsKey("writer.stream"));
    }

    @Test
    void prodConfig_usesRollingFileInLogDir() throws Exception {
        File logDir = new File(tempDir.toFile(), "log");
        Map<String, String> cfg = plugin.buildLoggingConfigForPrd(realLoggingProperties(), logDir);

        assertEquals("rolling file", cfg.get("writer"));
        assertEquals("trace", cfg.get("writer.level"));
        assertEquals("{date:HH:mm:ss.SSS} {level:min-size=5} {class-name} - {message}",
            cfg.get("writer.format"));
        assertEquals(new File(logDir, "vpcli.{date:yyyy-MM-dd}.{count}.log").getAbsolutePath(),
            cfg.get("writer.file"));
        // 不生成 latest 链接：用户期望只产出带占位符的滚动文件
        assertNull(cfg.get("writer.latest"));
        assertEquals("size: 5mb, daily", cfg.get("writer.policies"));
        assertEquals("30", cfg.get("writer.backups"));
        assertFalse(cfg.containsKey("writer.stream"));
    }

    @Test
    void configKeysComeFromProperties_overrideWriterType() {
        // 验证配置来自 Properties 而非硬编码：改 writer 类型后产物随之变化
        Properties props = new Properties();
        props.setProperty("dev.writer", "file");
        props.setProperty("writer.level", "info");
        props.setProperty("writer.format", "{message}");

        Map<String, String> cfg = plugin.buildLoggingConfigForDev(props);

        assertEquals("file", cfg.get("writer"));
        assertEquals("info", cfg.get("writer.level"));
        assertEquals("{message}", cfg.get("writer.format"));
    }

    @Test
    void prodConfig_readsPatternAndPoliciesFromProperties() {
        File logDir = new File(tempDir.toFile(), "log");
        Properties props = new Properties();
        props.setProperty("prod.writer", "rolling file");
        props.setProperty("prod.writer.file.pattern", "app.{pid}.log");
        props.setProperty("prod.writer.policies", "daily: 03:00, size: 20mb");
        props.setProperty("prod.writer.backups", "7");

        Map<String, String> cfg = plugin.buildLoggingConfigForPrd(props, logDir);

        assertEquals("rolling file", cfg.get("writer"));
        assertEquals(new File(logDir, "app.{pid}.log").getAbsolutePath(), cfg.get("writer.file"));
        assertEquals("daily: 03:00, size: 20mb", cfg.get("writer.policies"));
        assertEquals("7", cfg.get("writer.backups"));
    }

    @Test
    void resolvePluginLogDir_prefersPluginDir() {
        File pluginDir = new File(tempDir.toFile(), "plugin");
        VPPluginInfo info = mock(VPPluginInfo.class);
        when(info.getPluginDir()).thenReturn(pluginDir);

        File resolved = plugin.resolvePluginLogDir(info);

        assertEquals(new File(pluginDir, "log").getAbsolutePath(), resolved.getAbsolutePath());
        assertTrue(resolved.isDirectory());
    }

    @Test
    void resolvePluginLogDir_fallsBackToSystemProperty_whenNoPluginDir() {
        File fallback = new File(tempDir.toFile(), "fallback");
        System.setProperty("vpcli.log.dir", fallback.getAbsolutePath());

        File resolved = plugin.resolvePluginLogDir(null);

        assertEquals(fallback.getAbsolutePath(), resolved.getAbsolutePath());
    }

    @Test
    void resolvePluginLogDir_fallsBackToWorkingDirLog_whenNothingAvailable() {
        System.clearProperty("vpcli.log.dir");

        File resolved = plugin.resolvePluginLogDir(null);

        assertEquals(new File(System.getProperty("user.dir"), "log").getAbsolutePath(),
            resolved.getAbsolutePath());
    }

    @Test
    void defaultProfileIsDev() throws Exception {
        System.clearProperty("vpcli.log.profile");
        Map<String, String> cfg = plugin.buildLoggingConfigForDev(realLoggingProperties());
        assertEquals("console", cfg.get("writer"));
    }
}
