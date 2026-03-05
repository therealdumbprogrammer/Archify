package com.archify.generator.recipe.expander;

import com.archify.generator.domain.Architecture;
import com.archify.generator.domain.Database;
import com.archify.generator.domain.Entity;
import com.archify.generator.domain.Field;
import com.archify.generator.domain.Service;
import com.archify.generator.domain.enums.DatabaseType;
import com.archify.generator.domain.enums.FieldType;

import java.util.List;
import java.util.Map;

public class RestH2Expander implements RecipeExpander {
    @Override
    public Architecture expand(Map<String, Object> input) {
        String serviceName = required(input, "serviceName");
        String entityName = required(input, "entityName");

        Entity entity = new Entity(entityName, List.of(
                new Field("id", FieldType.LONG),
                new Field("name", FieldType.STRING),
                new Field("createdAt", FieldType.STRING)
        ));

        Service service = new Service();
        service.setName(serviceName);
        service.setDatabase(new Database(DatabaseType.H2));
        service.setEntities(List.of(entity));

        Architecture architecture = new Architecture();
        architecture.setServices(List.of(service));
        return architecture;
    }

    private String required(Map<String, Object> input, String key) {
        Object value = input.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Missing required input: " + key);
        }
        return value.toString();
    }
}
