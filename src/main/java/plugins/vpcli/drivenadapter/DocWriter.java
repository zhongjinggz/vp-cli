package plugins.vpcli.drivenadapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plugins.vpcli.domain.outputmodel.PumlDiagram;
import plugins.vpcli.util.MyIO;

import java.io.File;
import java.io.IOException;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class DocWriter {
    private static final Logger log = LoggerFactory.getLogger(DocWriter.class);
    private final MyIO io;

    @Inject
    public DocWriter(MyIO io) {
        this.io = io;
    }

    void markdown() {

    }

    public void plantUML(PumlDiagram puml, File parentDir) throws IOException {
        String fileName = puml.getName() + ".puml";
        String pumlText = puml.toString();

//        log.info("Writing file {}, under {} text={}", fileName, parentDir, pumlText);
        io.writeFile(parentDir, fileName, pumlText);
    }
}
