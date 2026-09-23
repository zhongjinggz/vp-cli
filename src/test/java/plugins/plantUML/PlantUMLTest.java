package plugins.plantUML;

import org.junit.jupiter.api.Test;

public class PlantUMLTest {

    private final PlantUML plugin = new PlantUML();

    @Test
    void loaded_doesNothing() {
        plugin.loaded(null);
    }

    @Test
    void unloaded_doesNothing() {
        plugin.unloaded();
    }
}