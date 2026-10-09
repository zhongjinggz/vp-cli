package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.structure;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;

import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.ViElement;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitor;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitorFactory;

import java.io.IOException;

class ViProjectFactoryTest {

    private final ViElementVisitorFactory visitorFactory = new ViElementVisitorFactory() {
        @Override
        public ViElementVisitor under(ViElementVisitor higherVisitor) {
            return new ViElementVisitor() {
                @Override
                public void visit(ViElement viElement) {
                    // no-op
                }
            };
        }

        @Override
        public ViElementVisitor createRoot() {
            return new ViElementVisitor() {
                @Override
                public void visit(ViElement viElement) {
                    // no-op
                }
            };
        }
    };

    @Test
    void shouldCreateVpStruct() {
        IProject project = mock(IProject.class);
        ViProject struct = new ViProjectFactory().of(project);
        assertNotNull(struct);
    }

    @Test
    void shouldAcceptProjectThroughCreatedStruct() throws IOException {
        IProject project = mock(IProject.class);
        when(project.toModelElementArray(new String[0])).thenReturn(new IModelElement[0]);
        ViProject struct = new ViProjectFactory().of(project);
        struct.accept(visitorFactory);
        verify(project).toModelElementArray(new String[0]);
    }
}
