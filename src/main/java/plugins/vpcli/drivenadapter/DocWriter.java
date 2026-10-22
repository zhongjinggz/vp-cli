package plugins.vpcli.drivenadapter;

import org.slf4j.Logger;
import plugins.vpcli.domain.outputmodel.PumlDiagram;
import plugins.vpcli.util.MyIO;

import java.io.File;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class DocWriter {
    private final Logger logger ;
    private final MyIO io;

    @Inject
    public DocWriter(MyIO myIO) {
        this(myIO, getLogger(DocWriter.class));
    }
    
    public DocWriter(MyIO io, Logger  logger) {
        this.io = io;
        this.logger = logger;
    }

    void markdown() {

    }

    public void plantUML(PumlDiagram puml, File parentDir) throws IOException {
        String fileName = puml.getName() + ".puml";
        String pumlText = puml.toString();

//        logger.info("Writing file {}, under {} text={}", fileName, parentDir, pumlText);
        io.writeFile(parentDir, fileName, pumlText);
    }
}
