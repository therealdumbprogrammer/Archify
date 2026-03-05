package com.archify.generator.recipe.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class RecipeLoader {
    private static final String RECIPE_PATH_PREFIX = "recipes/";
    private static final String YAML_SUFFIX = ".yaml";

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
}
