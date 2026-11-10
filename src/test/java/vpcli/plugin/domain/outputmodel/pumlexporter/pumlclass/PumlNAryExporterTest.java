package vpcli.plugin.domain.outputmodel.pumlexporter.pumlclass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.vp.plugin.diagram.IShapeTypeConstants;
import org.junit.jupiter.api.Test;

/**
 * 针对 {@link PumlNAryExporter}：把 n-ary 关联的枢纽渲染为
 * `diamond <别名>`，各参与类以带多重性的实线 `--` 连到菱形。
 */
class PumlNAryExporterTest {

    private final PumlNAryExporter exporter = new PumlNAryExporter();

    @Test
    void getShapeTypeReturnsNaryConstant() {
        assertEquals(IShapeTypeConstants.SHAPE_TYPE_NARY, exporter.getShapeType());
    }

}
