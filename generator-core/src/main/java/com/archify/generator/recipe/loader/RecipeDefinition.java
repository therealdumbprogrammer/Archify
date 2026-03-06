package com.archify.generator.recipe.loader;

import java.util.ArrayList;
import java.util.List;

public class RecipeDefinition {
    private String name;
    private String description;
    private List<RecipeInputDefinition> inputs = new ArrayList<>();
    private DiagramDefinition diagram;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<RecipeInputDefinition> getInputs() {
        return inputs;
    }

    public void setInputs(List<RecipeInputDefinition> inputs) {
        this.inputs = inputs;
    }

    public DiagramDefinition getDiagram() {
        return diagram;
    }

    public void setDiagram(DiagramDefinition diagram) {
        this.diagram = diagram;
    }
}
