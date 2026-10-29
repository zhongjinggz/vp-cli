package plugins.vpcli.domain.vpmodelvisitor;

import static com.vp.plugin.model.factory.IModelElementFactory.MODEL_TYPE_ABSTRACTION;
import static com.vp.plugin.model.factory.IModelElementFactory.MODEL_TYPE_BINDING_DEPENDENCY;
import static com.vp.plugin.model.factory.IModelElementFactory.MODEL_TYPE_DEPENDENCY;
import static com.vp.plugin.model.factory.IModelElementFactory.MODEL_TYPE_PERMISSION;
import static com.vp.plugin.model.factory.IModelElementFactory.MODEL_TYPE_USAGE;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * 步 #21：ViElementType 对 4 个独立 model type 的登记，
 * 其余 5 类（Access/Import/Merge/Instantiate/Substitute）复用 DEPENDENCY。
 */
class ViElementTypeSpecialDependencyTest {

    @Test
    void registersIndependentModelTypes() {
        assertArrayEquals(new String[]{
            MODEL_TYPE_USAGE, ".usage",
            MODEL_TYPE_BINDING_DEPENDENCY, ".binding-dependency",
            MODEL_TYPE_PERMISSION, ".permission",
            MODEL_TYPE_ABSTRACTION, ".abstraction"
        }, new String[]{
            ViElementType.USAGE.getVPModelType(), ViElementType.USAGE.getSuffix(),
            ViElementType.BINDING_DEPENDENCY.getVPModelType(), ViElementType.BINDING_DEPENDENCY.getSuffix(),
            ViElementType.PERMISSION.getVPModelType(), ViElementType.PERMISSION.getSuffix(),
            ViElementType.ABSTRACTION.getVPModelType(), ViElementType.ABSTRACTION.getSuffix(),
        });
    }

    @Test
    void ofResolvesIndependentModelTypes() {
        assertEquals(ViElementType.USAGE, ViElementType.of(MODEL_TYPE_USAGE));
        assertEquals(ViElementType.BINDING_DEPENDENCY, ViElementType.of(MODEL_TYPE_BINDING_DEPENDENCY));
        assertEquals(ViElementType.PERMISSION, ViElementType.of(MODEL_TYPE_PERMISSION));
        assertEquals(ViElementType.ABSTRACTION, ViElementType.of(MODEL_TYPE_ABSTRACTION));
        // 五个复用类型仍解析到 DEPENDENCY，且不影响普通依赖。
        assertEquals(ViElementType.DEPENDENCY, ViElementType.of(MODEL_TYPE_DEPENDENCY));
    }

    @Test
    void vpModelTypeIsHoldsForNewEntries() {
        assertTrue(ViElementType.USAGE.vpModelTypeIs(MODEL_TYPE_USAGE));
        assertTrue(ViElementType.BINDING_DEPENDENCY.vpModelTypeIs(MODEL_TYPE_BINDING_DEPENDENCY));
        assertTrue(ViElementType.PERMISSION.vpModelTypeIs(MODEL_TYPE_PERMISSION));
        assertTrue(ViElementType.ABSTRACTION.vpModelTypeIs(MODEL_TYPE_ABSTRACTION));
    }
}
