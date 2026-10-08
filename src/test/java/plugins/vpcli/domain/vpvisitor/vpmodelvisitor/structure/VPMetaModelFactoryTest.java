package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.structure;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;

import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.VPElement;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.VPElementVisitor;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.VPElementVisitorFactory;

import java.io.IOException;

class VPMetaModelFactoryTest {

    private final VPElementVisitorFactory visitorFactory = pre -> new VPElementVisitor() {
        @Override
        public void visit(VPElement vpElement) {
            // no-op
        }
    };

    @Test
    void shouldCreateVpStruct() {
        IProject project = mock(IProject.class);
        VPMetaModel struct = new VPMetaModelFactory().create(project, visitorFactory);
        assertNotNull(struct);
    }

    @Test
    void shouldAcceptProjectThroughCreatedStruct() throws IOException {
        IProject project = mock(IProject.class);
        when(project.toModelElementArray(new String[0])).thenReturn(new IModelElement[0]);
        VPMetaModel struct = new VPMetaModelFactory().create(project, visitorFactory);
        struct.accept(visitorFactory);
        verify(project).toModelElementArray(new String[0]);
    }
}
