package vpcli.plugin;

import org.junit.jupiter.api.Test;

public class VPCLITest {

    private final VPCLI plugin = new VPCLI();

    @Test
    void unloaded_doesNothing() {
        plugin.unloaded();
    }
}
