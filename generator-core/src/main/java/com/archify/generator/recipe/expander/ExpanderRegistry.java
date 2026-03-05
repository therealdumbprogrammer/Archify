package com.archify.generator.recipe.expander;

import java.util.HashMap;
import java.util.Map;

public class ExpanderRegistry {
    private final Map<String, RecipeExpander> expanders = new HashMap<>();

    public ExpanderRegistry() {
        expanders.put("rest-postgres", new RestPostgresExpander());
        expanders.put("rest-h2", new RestH2Expander());
        expanders.put("two-service-rest", new TwoServiceRestExpander());
    }

    public RecipeExpander get(String recipeName) {
        RecipeExpander expander = expanders.get(recipeName);
        if (expander == null) {
            throw new IllegalArgumentException("No expander registered for recipe: " + recipeName);
        }
        return expander;
    }
}
