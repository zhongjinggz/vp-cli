package plugins.vpcli;

import org.junit.jupiter.api.Test;

public class VPCLITest {

    private final VPCLI plugin = new VPCLI();

    @Test
    void loaded_doesNothing() {
        plugin.loaded(null);
    }

    @Test
    void unloaded_doesNothing() {
        plugin.unloaded();
    }
}