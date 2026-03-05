package com.archify.backend.api;

import com.archify.backend.service.GenerationService;
import com.archify.generator.recipe.loader.RecipeDefinition;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/recipes")
public class RecipeController {
    private final GenerationService generationService;

    public RecipeController(GenerationService generationService) {
        this.generationService = generationService;
    }

    @GetMapping
    public List<RecipeDefinition> listRecipes() {
        return generationService.listRecipes();
    }
}
