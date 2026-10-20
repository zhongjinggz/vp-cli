package plugins.vpcli.util;

import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.slf4j.LoggerFactory.getLogger;

public class MyIO {
    private final Logger logger;

    public MyIO() {
        this(getLogger(MyIO.class));
    }

    public MyIO(Logger logger) {
        this.logger = logger;
    }
    public void writeFile(File parentDir, String fileName, String pumlText) throws IOException {
        File outputFile = new File(parentDir, fileName);
        Path path = outputFile.toPath();
        logger.debug("Writing file {} to {}, text={}", fileName, path, pumlText);
        Files.writeString(path, pumlText, StandardCharsets.UTF_8);
    }
}
