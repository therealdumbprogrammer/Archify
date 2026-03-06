package com.archify.generator.recipe.loader;

import java.util.ArrayList;
import java.util.List;

public class DiagramDefinition {
    private List<DiagramNodeDefinition> nodes = new ArrayList<>();
    private List<DiagramEdgeDefinition> edges = new ArrayList<>();

    public List<DiagramNodeDefinition> getNodes() {
        return nodes;
    }

    public void setNodes(List<DiagramNodeDefinition> nodes) {
        this.nodes = nodes;
    }

    public List<DiagramEdgeDefinition> getEdges() {
        return edges;
    }

    public void setEdges(List<DiagramEdgeDefinition> edges) {
        this.edges = edges;
    }
}
