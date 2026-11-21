package vpcli.plugin;

import com.vp.plugin.VPPluginInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 锁定日志落点配置：{@link VPCLI#configLogWriterFile} 在插件加载时把 writer.file 指到
 * “插件安装目录下 log/” + 配置文件里的文件名模式；其余参数（级别、格式、滚动策略、
 * 备份数）均来自静态 /tinylog.properties。
 */
public class VPCLIConfigurationTest {

    @TempDir
    Path tempDir;

    private final VPCLI plugin = new VPCLI();

    @AfterEach
    void cleanSystemProps() {
        System.clearProperty("vpcli.log.dir");
    }

    @Test
    void writerFilePattern_comesFromProperties() throws Exception {
        Properties props = readProperties();
        assertEquals("vpcli.{date:yyyy-MM-dd}.{count}.log",
            props.getProperty("writer.file.pattern"));
        assertEquals("rolling file", props.getProperty("writer"));
        assertEquals("trace", props.getProperty("writer.level"));
        assertEquals("{date:HH:mm:ss.SSS} {level:min-size=5} {class-name} - {message}",
            props.getProperty("writer.format"));
        assertEquals("size: 5mb, daily", props.getProperty("writer.policies"));
        assertEquals("30", props.getProperty("writer.backups"));
        // 无 dev/prod 前缀，证明已归一为单配置
        assertTrue(props.getProperty("dev.writer") == null);
        assertTrue(props.getProperty("prod.writer") == null);
    }

    @Test
    void readWriterFilePattern_usesConfigValue() throws Exception {
        assertEquals("vpcli.{date:yyyy-MM-dd}.{count}.log",
            plugin.readWriterFilePattern());
    }


    @Test
    void resolvePluginLogDir_fallsBackToSystemTempDir_whenNothingAvailable() {
        Path resolved = plugin.resolvePluginLogDir(null);
        assertEquals(Paths.get(System.getProperty("java.io.tmpdir")).toAbsolutePath(),
            resolved.toAbsolutePath());
    }

    private Properties readProperties() throws Exception {
        Properties props = new Properties();
        try (java.io.InputStream in =
                 VPCLI.class.getResourceAsStream("/tinylog.properties")) {
            assertTrue(in != null, "缺少 classpath 根下的 tinylog.properties");
            props.load(in);
        }
        return props;
    }
}
