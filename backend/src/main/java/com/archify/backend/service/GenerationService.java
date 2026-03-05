package com.archify.backend.service;

import com.archify.generator.ArchifyGeneratorFacade;
import com.archify.generator.recipe.loader.RecipeDefinition;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GenerationService {
    private final ArchifyGeneratorFacade generatorFacade = new ArchifyGeneratorFacade();

    public byte[] generate(String recipe, Map<String, Object> config) {
        return generatorFacade.generateZip(recipe, config);
    }

    public List<RecipeDefinition> listRecipes() {
        return generatorFacade.listRecipes();
    }
}
