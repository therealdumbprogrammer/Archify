package com.archify.generator.recipe.expander;

import com.archify.generator.domain.Architecture;
import com.archify.generator.domain.Database;
import com.archify.generator.domain.Entity;
import com.archify.generator.domain.Service;
import com.archify.generator.domain.ServiceCall;
import com.archify.generator.domain.enums.DatabaseType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TwoServiceRestExpander implements RecipeExpander {
    @Override
    public Architecture expand(Map<String, Object> input) {
        String serviceAName = RecipeConfigSupport.requiredString(input, "serviceAName");
        String serviceBName = RecipeConfigSupport.requiredString(input, "serviceBName");
        String communication = RecipeConfigSupport.requiredString(input, "communication");
        String database = RecipeConfigSupport.requiredString(input, "database");
        List<Entity> entities = RecipeConfigSupport.requiredEntities(input);

        if (!"FEIGN".equalsIgnoreCase(communication)) {
            throw new IllegalArgumentException("Unsupported communication type: " + communication);
        }

        DatabaseType databaseType = switch (database.toUpperCase(Locale.ROOT)) {
            case "NONE" -> DatabaseType.NONE;
            case "POSTGRES" -> DatabaseType.POSTGRES;
            default -> throw new IllegalArgumentException("Unsupported database type: " + database);
        };

        Service serviceA = new Service();
        serviceA.setName(serviceAName);
        serviceA.setDatabase(new Database(databaseType));
        serviceA.setEntities(cloneEntities(entities));

        Service serviceB = new Service();
        serviceB.setName(serviceBName);
        serviceB.setDatabase(new Database(databaseType));
        serviceB.setEntities(cloneEntities(entities));
        String basePath = "/" + entities.get(0).getName().toLowerCase(Locale.ROOT) + "s/{id}";
        serviceB.setCalls(List.of(new ServiceCall(serviceAName, basePath)));

        Architecture architecture = new Architecture();
        architecture.setServices(List.of(serviceA, serviceB));
        return architecture;
    }

    private List<Entity> cloneEntities(List<Entity> entities) {
        List<Entity> cloned = new ArrayList<>();
        for (Entity entity : entities) {
            cloned.add(new Entity(entity.getName(), new ArrayList<>(entity.getFields())));
        }
        return cloned;
    }
}
