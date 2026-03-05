package com.archify.generator.recipe.expander;

import com.archify.generator.domain.Entity;
import com.archify.generator.domain.Field;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

final class RecipeConfigSupport {
    private static final Set<String> SUPPORTED_TYPES = Set.of(
            "string", "long", "integer", "boolean", "double", "localdate", "localdatetime", "bigdecimal"
    );

    private RecipeConfigSupport() {
    }

    static String requiredString(Map<String, Object> input, String key) {
        Object value = input.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Missing required input: " + key);
        }
        return value.toString().trim();
    }

    static List<Entity> requiredEntities(Map<String, Object> input) {
        Object raw = input.get("entities");
        if (!(raw instanceof List<?> entitiesRaw) || entitiesRaw.isEmpty()) {
            throw new IllegalArgumentException("Missing required input: entities");
        }

        List<Entity> entities = new ArrayList<>();
        Set<String> names = new HashSet<>();
        for (Object item : entitiesRaw) {
            if (!(item instanceof Map<?, ?> entityMap)) {
                throw new IllegalArgumentException("Each entity must be an object");
            }
            String entityName = requiredString(castMap(entityMap), "name");
            if (!names.add(entityName.toLowerCase(Locale.ROOT))) {
                throw new IllegalArgumentException("Duplicate entity name: " + entityName);
            }
            entities.add(new Entity(entityName, normalizeFields(castMap(entityMap))));
        }

        return entities;
    }

    private static List<Field> normalizeFields(Map<String, Object> entityMap) {
        Object raw = entityMap.get("fields");
        if (!(raw instanceof List<?> fieldsRaw)) {
            throw new IllegalArgumentException("Entity fields must be a list");
        }

        List<Field> fields = new ArrayList<>();
        boolean hasId = false;
        Set<String> names = new HashSet<>();

        for (Object fieldObject : fieldsRaw) {
            if (!(fieldObject instanceof Map<?, ?> fieldMap)) {
                throw new IllegalArgumentException("Each field must be an object");
            }

            Map<String, Object> normalizedMap = castMap(fieldMap);
            String fieldName = requiredString(normalizedMap, "name");
            String type = requiredString(normalizedMap, "type");
            String canonicalType = canonicalType(type);

            if (!names.add(fieldName.toLowerCase(Locale.ROOT))) {
                throw new IllegalArgumentException("Duplicate field name: " + fieldName);
            }
            if (fieldName.equalsIgnoreCase("id")) {
                hasId = true;
                fields.add(new Field("id", "Long"));
            } else {
                fields.add(new Field(fieldName, canonicalType));
            }
        }

        if (!hasId) {
            fields.add(0, new Field("id", "Long"));
        }

        return fields;
    }

    static String canonicalType(String input) {
        String normalized = input.trim().toLowerCase(Locale.ROOT);
        if (!SUPPORTED_TYPES.contains(normalized)) {
            throw new IllegalArgumentException("Unsupported field type: " + input);
        }

        return switch (normalized) {
            case "string" -> "String";
            case "long" -> "Long";
            case "integer" -> "Integer";
            case "boolean" -> "Boolean";
            case "double" -> "Double";
            case "localdate" -> "LocalDate";
            case "localdatetime" -> "LocalDateTime";
            case "bigdecimal" -> "BigDecimal";
            default -> throw new IllegalArgumentException("Unsupported field type: " + input);
        };
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> castMap(Map<?, ?> map) {
        return (Map<String, Object>) map;
    }
}
