package plugins.vpcli.domain.myuml.mydiagram;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class MyDiagram {
    private String vpId;
    private String name;
    private DiagramType diagramType;
    private List<MyShape> shapes = new ArrayList<>();
    private List<MyEdge> edges  = new ArrayList<>();
    
    
    
    public MyDiagram(@NotNull String vpId
        , @NotNull String name
        , @NotNull DiagramType diagramType) {

        Objects.requireNonNull(vpId, "vpId is null");
        Objects.requireNonNull(name, "name is null");
        Objects.requireNonNull(diagramType, "diagramType is null");

        this.vpId = vpId;
        this.name = name;
        this.diagramType = diagramType;
    }
    
    public DiagramType getType() {
        return diagramType;
    }

    public List<MyShape> getShapes() {
        return Collections.unmodifiableList(shapes);
    }

    public List<MyEdge> getEdges() {
        return Collections.unmodifiableList(edges);
    }

    public void addShap(@NotNull MyShape shap) {
        Objects.requireNonNull(shap, "shap is null");
        shapes.add(shap);
    }

    public void addEdge(@NotNull MyEdge edge) {
        Objects.requireNonNull(edge, "edge is null");
        edges.add(edge);
    }
}
