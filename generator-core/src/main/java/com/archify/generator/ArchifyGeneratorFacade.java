package com.archify.generator;

import com.archify.generator.domain.Architecture;
import com.archify.generator.generation.FileLeaf;
import com.archify.generator.generation.ProjectGenerator;
import com.archify.generator.generation.ServiceGenerator;
import com.archify.generator.packaging.ZipAssembler;
import com.archify.generator.recipe.expander.ExpanderRegistry;
import com.archify.generator.recipe.expander.RecipeExpander;
import com.archify.generator.recipe.loader.RecipeDefinition;
import com.archify.generator.recipe.loader.RecipeLoader;
import com.archify.generator.template.TemplateEngine;
import com.archify.generator.validation.ArchitectureValidator;

import java.util.List;
import java.util.Map;

public class ArchifyGeneratorFacade {
    private final RecipeLoader recipeLoader;
    private final ExpanderRegistry expanderRegistry;
    private final ArchitectureValidator validator;
    private final ProjectGenerator projectGenerator;
    private final ZipAssembler zipAssembler;

    public ArchifyGeneratorFacade() {
        this.recipeLoader = new RecipeLoader();
        this.expanderRegistry = new ExpanderRegistry();
        this.validator = new ArchitectureValidator();
        this.projectGenerator = new ProjectGenerator(new ServiceGenerator(new TemplateEngine()));
        this.zipAssembler = new ZipAssembler();
    }

    public byte[] generateZip(String recipe, Map<String, Object> config) {
        RecipeDefinition definition = recipeLoader.load(recipe);
        validateInputs(definition, config);

        RecipeExpander expander = expanderRegistry.get(definition.getName());
        Architecture architecture = expander.expand(config);
        validator.validate(architecture);

        List<FileLeaf> files = projectGenerator.generate(architecture);
        return zipAssembler.zip(files);
    }

    private void validateInputs(RecipeDefinition definition, Map<String, Object> config) {
        for (String input : definition.getInputs()) {
            Object value = config.get(input);
            if (value == null || value.toString().isBlank()) {
                throw new IllegalArgumentException("Missing required input for recipe '" + definition.getName() + "': " + input);
            }
        }
    }
}
