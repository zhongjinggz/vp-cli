package vpcli.plugin.domain.outputmodel.pumlexporter.fundation;

import com.vp.plugin.diagram.IDiagramElement;

public interface IPumlElementExporterRepository {
    PumlElementExporter findBy(IDiagramElement vpDiagramElement);
}
