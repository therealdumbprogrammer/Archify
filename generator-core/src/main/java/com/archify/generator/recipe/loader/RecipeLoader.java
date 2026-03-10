package com.archify.generator.recipe.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class RecipeLoader {
    private static final String RECIPE_PATH_PREFIX = "recipes/";
    private static final String YAML_SUFFIX = ".yaml";
    private static final List<String> KNOWN_RECIPES = List.of(
            "jdbc-template-h2",
            "jdbc-template-postgres",
            "rest-h2",
            "rest-postgres",
            "two-service-rest");

    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    public RecipeDefinition load(String recipeName) {
        String normalizedName = recipeName.toLowerCase(Locale.ROOT);
        String path = RECIPE_PATH_PREFIX + normalizedName + YAML_SUFFIX;
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Recipe not found: " + recipeName);
            }
            return yamlMapper.readValue(inputStream, RecipeDefinition.class);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load recipe: " + recipeName, exception);
        }
    }

    public List<RecipeDefinition> loadAll() {
        List<RecipeDefinition> definitions = new ArrayList<>();
        for (String recipeName : KNOWN_RECIPES) {
            definitions.add(load(recipeName));
        }
        definitions.sort(Comparator.comparing(RecipeDefinition::getName));
        return definitions;
    }
}
