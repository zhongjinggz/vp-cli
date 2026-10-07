package plugins.vpcli.domain.vpstruct;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;

import org.junit.jupiter.api.Test;

import java.io.IOException;

class VPStructFactoryTest {

    private final VPVisitorFactory visitorFactory = pre -> new VPVisitor() {
        @Override
        public void visit(VPStructElement vpStructElement) {
            // no-op
        }
    };

    @Test
    void shouldCreateVpStruct() {
        IProject project = mock(IProject.class);
        VPStruct struct = new VPStructFactory().create(project, visitorFactory);
        assertNotNull(struct);
    }

    @Test
    void shouldAcceptProjectThroughCreatedStruct() throws IOException {
        IProject project = mock(IProject.class);
        when(project.toModelElementArray(new String[0])).thenReturn(new IModelElement[0]);
        VPStruct struct = new VPStructFactory().create(project, visitorFactory);
        struct.accept(visitorFactory);
        verify(project).toModelElementArray(new String[0]);
    }
}
