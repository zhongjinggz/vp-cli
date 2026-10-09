package plugins.vpcli;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class VPCLITest {

    private final VPCLI plugin = new VPCLI();

    @AfterEach
    void cleanSystemProps() {
        System.clearProperty("vpcli.log.appender");
        System.clearProperty("vpcli.log.dir");
        System.clearProperty("vpcli.log.profile");
    }

    @Test
    void loaded_withNullPluginInfo_setsDevAppender() {
        plugin.loaded(null);

        assertEquals("CONSOLE", System.getProperty("vpcli.log.appender"));
    }

    @Test
    void loaded_withNullPluginInfo_noLogDirSet() {
        plugin.loaded(null);

        // null pluginInfo 没有可用的插件目录，不能设置 vpcli.log.dir
        assertNull(System.getProperty("vpcli.log.dir"));
    }

    @Test
    void unloaded_doesNothing() {
        plugin.unloaded();
    }
}