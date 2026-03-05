package com.archify.generator.recipe.loader;

import java.util.ArrayList;
import java.util.List;

public class RecipeDefinition {
    private String name;
    private String description;
    private List<String> inputs = new ArrayList<>();

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

    public List<String> getInputs() {
        return inputs;
    }

    public void setInputs(List<String> inputs) {
        this.inputs = inputs;
    }
}
