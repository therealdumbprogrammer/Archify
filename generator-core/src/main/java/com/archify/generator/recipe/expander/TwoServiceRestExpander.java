package com.archify.generator.recipe.expander;

import com.archify.generator.domain.Architecture;
import com.archify.generator.domain.Database;
import com.archify.generator.domain.Entity;
import com.archify.generator.domain.Field;
import com.archify.generator.domain.Service;
import com.archify.generator.domain.ServiceCall;
import com.archify.generator.domain.enums.DatabaseType;
import com.archify.generator.domain.enums.FieldType;

import java.util.List;
import java.util.Map;

public class TwoServiceRestExpander implements RecipeExpander {
    @Override
    public Architecture expand(Map<String, Object> input) {
        String serviceAName = required(input, "serviceAName");
        String serviceBName = required(input, "serviceBName");
        String entityName = required(input, "entityName");
        boolean usePostgres = Boolean.parseBoolean(String.valueOf(input.getOrDefault("usePostgres", "false")));

        Service serviceA = new Service();
        serviceA.setName(serviceAName);
        if (usePostgres) {
            serviceA.setDatabase(new Database(DatabaseType.POSTGRES));
        }
        serviceA.setEntities(List.of(new Entity(entityName, List.of(
                new Field("id", FieldType.LONG),
                new Field("name", FieldType.STRING)
        ))));

        Service serviceB = new Service();
        serviceB.setName(serviceBName);
        if (usePostgres) {
            serviceB.setDatabase(new Database(DatabaseType.POSTGRES));
        }
        serviceB.setEntities(List.of(new Entity(entityName + "View", List.of(
                new Field("id", FieldType.LONG),
                new Field("displayName", FieldType.STRING)
        ))));
        serviceB.setCalls(List.of(new ServiceCall(serviceAName, "/" + entityName.toLowerCase() + "/{id}")));

        Architecture architecture = new Architecture();
        architecture.setServices(List.of(serviceA, serviceB));
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
