package plugins.vpcli.domain.outputmodel.pumlexporter.pumldependency;

import com.vp.plugin.diagram.IShapeTypeConstants;
import org.jspecify.annotations.NonNull;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
//public class PumlRealizationExporter extends PumlElementExporter {
public class PumlRealizationExporter extends PumlDependencyExporter {
    @Inject
    public PumlRealizationExporter() {
    }

    @Override
    public String getShapeType() {
        return IShapeTypeConstants.SHAPE_TYPE_REALIZATION;
    }

    @Override
    protected  @NonNull String getArrow() {
        return " <|.. ";
    }
}
