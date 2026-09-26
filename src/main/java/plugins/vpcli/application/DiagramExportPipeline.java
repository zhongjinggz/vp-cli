package plugins.vpcli.application;
import com.vp.plugin.ProjectManager;
import com.vp.plugin.diagram.IDiagramUIModel;

import plugins.vpcli.application.exporter.ExporterFactory;
import plugins.vpcli.drivenadapter.FileFactory;
import plugins.vpcli.drivenadapter.ProjectManagerFactory;
import plugins.vpcli.application.writers.WriterFactory;
import plugins.vpcli.application.exporter.*;
import plugins.vpcli.application.writers.*;
import plugins.vpcli.domain.SemanticsData;
import plugins.vpcli.util.UnfitForExportException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiagramExportPipeline {

	private final ProjectManagerFactory projectManagerFactory;
	private final ExporterFactory exporterFactory;
	private final WriterFactory writerFactory;
	private final FileFactory fileFactory;

	public DiagramExportPipeline(ProjectManagerFactory projectManagerFactory, ExporterFactory exporterFactory, WriterFactory writerFactory, FileFactory fileFactory) {
		this.projectManagerFactory = projectManagerFactory;
		this.exporterFactory = exporterFactory;
		this.writerFactory = writerFactory;
		this.fileFactory = fileFactory;
	}

	private final List<SemanticsData> projectSemanticsDatas = new ArrayList<>();

	public void export(IDiagramUIModel diagram, File exportLocation) throws IOException, UnfitForExportException {
		String diagramType = diagram.getType();
		String diagramTitle = diagram.getName();
		File outputFile = createOutputFile(diagramTitle, "uml", exportLocation);

        try {
			switch (diagramType) {
			case "ClassDiagram":
				ClassDiagramExporter cde = exporterFactory.createClassDiagramExporter(diagram);
                cde.extract();
				ClassUMLWriter classWriter = writerFactory.createClassUMLWriter(cde);
				classWriter.writeToFile(outputFile);

				if (cde.getExportedSemantics() != null && !cde.getExportedSemantics().isEmpty()) {
					projectSemanticsDatas.addAll(cde.getExportedSemantics());
				}

				break;

			case "ComponentDiagram":
			case "DeploymentDiagram":
				ComponentDeploymentDiagramExporter exporter = exporterFactory.createComponentDeploymentDiagramExporter(diagram);
                exporter.extract();
				ComponentDeploymentUMLWriter componentWriter = writerFactory.createComponentDeploymentUMLWriter(exporter);
				componentWriter.writeToFile(outputFile);

				if (exporter.getExportedSemantics() != null && !exporter.getExportedSemantics().isEmpty()) {
					projectSemanticsDatas.addAll(exporter.getExportedSemantics());
				}
				break;

			case "InteractionDiagram":
				SequenceDiagramExporter seqde = exporterFactory.createSequenceDiagramExporter(diagram);
                seqde.extract();
				SequenceUMLWriter sequenceWriter = writerFactory.createSequenceUMLWriter(seqde);
				sequenceWriter.writeToFile(outputFile);

				if (seqde.getExportedSemantics() != null && !seqde.getExportedSemantics().isEmpty()) {
					projectSemanticsDatas.addAll(seqde.getExportedSemantics());
				}
				
				break;

			case "UseCaseDiagram":
				UseCaseDiagramExporter ucde = exporterFactory.createUseCaseDiagramExporter(diagram);
                ucde.extract();
				UseCaseWriter useCaseWriter = writerFactory.createUseCaseWriter(ucde);
				useCaseWriter.writeToFile(outputFile);
				if (ucde.getExportedSemantics() != null && !ucde.getExportedSemantics().isEmpty()) {
					projectSemanticsDatas.addAll(ucde.getExportedSemantics());
				}

				break;
			case "StateDiagram":
				StateDiagramExporter stde = exporterFactory.createStateDiagramExporter(diagram);
                stde.extract();
				StateUMLWriter stateUMLWriter = writerFactory.createStateUMLWriter(stde);
				stateUMLWriter.writeToFile(outputFile);
				if (stde.getExportedSemantics() != null && !stde.getExportedSemantics().isEmpty()) {

					projectSemanticsDatas.addAll(stde.getExportedSemantics());
				}

				break;
			case "ActivityDiagram":
				ActivityDiagramExporter acde = exporterFactory.createActivityDiagramExporter(diagram);
                acde.extract();
				ActivityUMLWriter activityUMLWriter = writerFactory.createActivityUMLWriter(acde);
				activityUMLWriter.writeToFile(outputFile);
				if (acde.getExportedSemantics() != null && !acde.getExportedSemantics().isEmpty()) {
					projectSemanticsDatas.addAll(acde.getExportedSemantics());
				}
				break;
			default:
				throw new UnfitForExportException("Error: " + diagramType + " not supported for export yet.");
			}
		} catch (IOException | UnfitForExportException ex) {
			//TODO 统一异常处理
			throw ex;

		}
    }

	public List<SemanticsData> exportPartialSemantics(DiagramExporter diagramExporter) {
		if (diagramExporter.getExportedSemantics() != null && !diagramExporter.getExportedSemantics().isEmpty()) {
			return diagramExporter.getExportedSemantics(); 
		}
		return null;
	}

	File createOutputFile(String title, String contentType, File exportLocation) throws IOException {
		StringBuilder fileName = new StringBuilder();
		// 放行所有语言的字母和数字（中文文件名）；空格、符号及 Windows 保留字符仍转下划线
		fileName.append(title.replaceAll("[^\\p{L}\\p{N}]", "_"));
		if (contentType.equals("json")) fileName.append("_semantics");
		fileName.append(".puml");
		File outputFile = fileFactory.createFile(exportLocation, fileName.toString());
		if (!outputFile.exists() && !outputFile.createNewFile()) {
			throw new IOException("Failed to create file: " + outputFile.getAbsolutePath());
		}
		return outputFile;
	}

	public boolean exportDiagramList(List<IDiagramUIModel> selectedDiagrams, File exportLocation) {
		boolean allSuccessful = true;
		for (IDiagramUIModel activeDiagram : selectedDiagrams) {
			try {
				export(activeDiagram, exportLocation);
			} catch (IOException | UnsupportedOperationException | UnfitForExportException ex) {
				//TODO 统一异常处理
				allSuccessful = false;
			}
        }

		File jsonFile;
		try {
			jsonFile = createOutputFile("project_semantics", "json", exportLocation);
			PlantJSONWriter.writeToFile(jsonFile, projectSemanticsDatas);
		} catch (IOException e) {
			//TODO 统一处理异常
			allSuccessful = false;
		}

		return allSuccessful;
	}

	public void exportAllDiagrams(File exportLocation) {
		ProjectManager projectManager = projectManagerFactory.getProjectManager();
		IDiagramUIModel[] allDiagrams = projectManager.getProject().toDiagramArray();
		this.exportDiagramList(Arrays.asList(allDiagrams), exportLocation);
	}

	public void exportSpecificDiagram(String target, File exportLocation) throws IOException {
		ProjectManager projectManager = projectManagerFactory.getProjectManager();
		IDiagramUIModel targetDiagram = projectManager.getProject().getDiagramById(target);
		this.export(targetDiagram, exportLocation);
	}

    public void listDiagrams() {
		ProjectManager projectManager = projectManagerFactory.getProjectManager();
		IDiagramUIModel[] allDiagrams = projectManager.getProject().toDiagramArray();
        for (IDiagramUIModel diagram : allDiagrams) {
            System.out.println(diagram.getName() + " | id: " + diagram.getId());
        }
    }
}