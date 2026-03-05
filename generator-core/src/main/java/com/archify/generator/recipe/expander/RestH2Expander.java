package com.archify.generator.recipe.expander;

import com.archify.generator.domain.Architecture;
import com.archify.generator.domain.Database;
import com.archify.generator.domain.Service;
import com.archify.generator.domain.enums.DatabaseType;

import java.util.List;
import java.util.Map;

public class RestH2Expander implements RecipeExpander {
    @Override
    public Architecture expand(Map<String, Object> input) {
        String serviceName = RecipeConfigSupport.requiredString(input, "serviceName");

        Service service = new Service();
        service.setName(serviceName);
        service.setDatabase(new Database(DatabaseType.H2));
        service.setEntities(RecipeConfigSupport.requiredEntities(input));

        Architecture architecture = new Architecture();
        architecture.setServices(List.of(service));
        return architecture;
    }
}
