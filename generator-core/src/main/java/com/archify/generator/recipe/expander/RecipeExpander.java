package com.archify.generator.recipe.expander;

import com.archify.generator.domain.Architecture;

import java.util.Map;

public interface RecipeExpander {
    Architecture expand(Map<String, Object> input);
}
