package com.archify.generator;

import com.archify.generator.domain.Architecture;
import com.archify.generator.generation.FileLeaf;
import com.archify.generator.generation.ProjectGenerator;
import com.archify.generator.generation.ServiceGenerator;
import com.archify.generator.packaging.ZipAssembler;
import com.archify.generator.recipe.expander.ExpanderRegistry;
import com.archify.generator.recipe.expander.RecipeExpander;
import com.archify.generator.recipe.loader.RecipeDefinition;
import com.archify.generator.recipe.loader.RecipeInputDefinition;
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
        TemplateEngine templateEngine = new TemplateEngine();
        this.projectGenerator = new ProjectGenerator(new ServiceGenerator(templateEngine), templateEngine);
        this.zipAssembler = new ZipAssembler();
    }

    public byte[] generateZip(String recipe, Map<String, Object> config) {
        RecipeDefinition definition = recipeLoader.load(recipe);
        validateInputs(definition, config);

        RecipeExpander expander = expanderRegistry.get(definition.getName());
        Architecture architecture = expander.expand(config);
        validator.validate(architecture);

        List<FileLeaf> files = projectGenerator.generate(definition.getName(), architecture);
        return zipAssembler.zip(files);
    }

    public List<RecipeDefinition> listRecipes() {
        return recipeLoader.loadAll();
    }

    private void validateInputs(RecipeDefinition definition, Map<String, Object> config) {
        for (RecipeInputDefinition input : definition.getInputs()) {
            if (!input.isRequired()) {
                continue;
            }
            Object value = config.get(input.getName());
            if (value == null || (value instanceof String stringValue && stringValue.isBlank())) {
                throw new IllegalArgumentException(
                        "Missing required input for recipe '" + definition.getName() + "': " + input.getName());
            }
        }
    }
}
