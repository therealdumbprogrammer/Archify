package com.archify.generator.generation;

import com.archify.generator.domain.Database;
import com.archify.generator.domain.Entity;
import com.archify.generator.domain.Field;
import com.archify.generator.domain.Service;
import com.archify.generator.domain.ServiceCall;
import com.archify.generator.domain.enums.DatabaseType;
import com.archify.generator.domain.enums.PersistenceStyle;
import com.archify.generator.template.TemplateEngine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ServiceGenerator {
    private final TemplateEngine templateEngine;

    public ServiceGenerator(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public List<FileLeaf> generate(String rootDir, Service service) {
        List<FileLeaf> files = new ArrayList<>();
        String serviceDir = rootDir + "/" + service.getName();
        String packageName = "com.archify." + sanitizePackageSegment(service.getName());
        String packagePath = packageName.replace('.', '/');

        Map<String, Object> baseModel = new HashMap<>();
        baseModel.put("serviceName", service.getName());
        baseModel.put("packageName", packageName);
        baseModel.put("database", service.getDatabase() == null ? "NONE" : service.getDatabase().getType().name());
        baseModel.put("feignEnabled", !service.getCalls().isEmpty());
        baseModel.put("persistenceStyle", service.getPersistenceStyle().name());

        files.add(new FileLeaf(serviceDir + "/pom.xml", templateEngine.render("artifacts/pom.ftl", baseModel)));
        files.add(new FileLeaf(serviceDir + "/src/main/java/" + packagePath + "/Application.java",
                templateEngine.render("artifacts/application.ftl", baseModel)));

        Map<String, Object> appModel = new HashMap<>(baseModel);
        appModel.put("springDatasource", datasourceSnippet(service));
        files.add(new FileLeaf(serviceDir + "/src/main/resources/application.yml",
                templateEngine.render("artifacts/application-yml.ftl", appModel)));

        if (service.getPersistenceStyle() == PersistenceStyle.JDBC_TEMPLATE) {
            files.add(new FileLeaf(
                    serviceDir + "/src/main/resources/schema.sql",
                    buildSchemaSql(service.getEntities())
            ));
        }

        files.addAll(generateEntities(service, serviceDir, packagePath, baseModel));
        files.addAll(generateFeignClients(service, serviceDir, packagePath, baseModel));
        files.addAll(generateMavenWrapper(serviceDir));

        return files;
    }

    private List<FileLeaf> generateEntities(Service service, String serviceDir, String packagePath, Map<String, Object> baseModel) {
        List<FileLeaf> files = new ArrayList<>();
        service.getEntities().stream()
                .sorted(Comparator.comparing(Entity::getName))
                .forEach(entity -> {
                    List<Field> normalizedFields = normalizeFields(entity.getFields());
                    Map<String, Object> entityModel = new HashMap<>(baseModel);
                    entityModel.put("entityName", entity.getName());
                    entityModel.put("entityVar", decapitalize(entity.getName()));
                    entityModel.put("entityPath", pluralize(decapitalize(entity.getName())));
                    entityModel.put("tableName", pluralize(decapitalize(entity.getName())));
                    entityModel.put("fields", mapFields(normalizedFields));
                    entityModel.put("imports", requiredImports(normalizedFields));
                    entityModel.put("nonIdFields", nonIdFields(normalizedFields));
                    entityModel.put("selectColumns", selectColumns(normalizedFields));
                    entityModel.put("insertColumns", insertColumns(normalizedFields));
                    entityModel.put("insertPlaceholders", insertPlaceholders(normalizedFields));
                    entityModel.put("updatableFields", updatableFields(normalizedFields));
                    entityModel.put("generatedIdColumn", "id");

                    String templateRoot = service.getPersistenceStyle() == PersistenceStyle.JDBC_TEMPLATE
                            ? "artifacts/jdbc/"
                            : "artifacts/";

                    files.add(new FileLeaf(
                            serviceDir + "/src/main/java/" + packagePath + "/entity/" + entity.getName() + ".java",
                            templateEngine.render(templateRoot + "entity.ftl", entityModel)
                    ));
                    files.add(new FileLeaf(
                            serviceDir + "/src/main/java/" + packagePath + "/repository/" + entity.getName() + "Repository.java",
                            templateEngine.render(templateRoot + "repository.ftl", entityModel)
                    ));
                    files.add(new FileLeaf(
                            serviceDir + "/src/main/java/" + packagePath + "/service/" + entity.getName() + "Service.java",
                            templateEngine.render(templateRoot + "service.ftl", entityModel)
                    ));
                    files.add(new FileLeaf(
                            serviceDir + "/src/main/java/" + packagePath + "/controller/" + entity.getName() + "Controller.java",
                            templateEngine.render(templateRoot + "controller.ftl", entityModel)
                    ));
                });

        return files;
    }

    private List<FileLeaf> generateFeignClients(Service service, String serviceDir, String packagePath, Map<String, Object> baseModel) {
        List<FileLeaf> files = new ArrayList<>();

        service.getCalls().stream()
                .sorted(Comparator.comparing(ServiceCall::getTargetService).thenComparing(ServiceCall::getPath))
                .forEach(call -> {
                    Map<String, Object> model = new HashMap<>(baseModel);
                    model.put("targetService", call.getTargetService());
                    model.put("targetClientName", capitalize(sanitizePackageSegment(call.getTargetService())) + "Client");
                    model.put("callPath", call.getPath());

                    files.add(new FileLeaf(
                            serviceDir + "/src/main/java/" + packagePath + "/client/" + model.get("targetClientName") + ".java",
                            templateEngine.render("artifacts/feign-client.ftl", model)
                    ));
                });

        return files;
    }

    private List<FileLeaf> generateMavenWrapper(String serviceDir) {
        List<FileLeaf> files = new ArrayList<>();
        files.add(new FileLeaf(serviceDir + "/mvnw", templateEngine.render("artifacts/mvnw.ftl", Map.of())));
        files.add(new FileLeaf(serviceDir + "/mvnw.cmd", templateEngine.render("artifacts/mvnw.cmd.ftl", Map.of())));
        files.add(new FileLeaf(serviceDir + "/.mvn/wrapper/maven-wrapper.properties",
                templateEngine.render("artifacts/maven-wrapper.properties.ftl", Map.of())));
        return files;
    }

    private List<Map<String, Object>> mapFields(List<Field> fields) {
        List<Map<String, Object>> mapped = new ArrayList<>();
        for (Field field : fields) {
            Map<String, Object> value = new HashMap<>();
            value.put("name", field.getName());
            value.put("javaType", canonicalFieldType(field.getType()));
            value.put("sqlType", sqlFieldType(field.getType(), "id".equalsIgnoreCase(field.getName())));
            mapped.add(value);
        }
        mapped.sort(Comparator.comparing(v -> (String) v.get("name")));
        return mapped;
    }

    private List<String> requiredImports(List<Field> fields) {
        Set<String> imports = new HashSet<>();
        for (Field field : fields) {
            String type = canonicalFieldType(field.getType());
            if ("LocalDate".equals(type)) {
                imports.add("java.time.LocalDate");
            }
            if ("LocalDateTime".equals(type)) {
                imports.add("java.time.LocalDateTime");
            }
            if ("BigDecimal".equals(type)) {
                imports.add("java.math.BigDecimal");
            }
        }
        return imports.stream().sorted().toList();
    }

    private String canonicalFieldType(String type) {
        return switch (type.toLowerCase(Locale.ROOT)) {
            case "string" -> "String";
            case "long" -> "Long";
            case "integer" -> "Integer";
            case "boolean" -> "Boolean";
            case "double" -> "Double";
            case "localdate" -> "LocalDate";
            case "localdatetime" -> "LocalDateTime";
            case "bigdecimal" -> "BigDecimal";
            default -> throw new IllegalArgumentException("Unsupported field type: " + type);
        };
    }

    private String sqlFieldType(String type, boolean idField) {
        if (idField) {
            return "BIGINT";
        }
        return switch (type.toLowerCase(Locale.ROOT)) {
            case "string" -> "VARCHAR(255)";
            case "long" -> "BIGINT";
            case "integer" -> "INTEGER";
            case "boolean" -> "BOOLEAN";
            case "double" -> "DOUBLE PRECISION";
            case "localdate" -> "DATE";
            case "localdatetime" -> "TIMESTAMP";
            case "bigdecimal" -> "DECIMAL(19,2)";
            default -> throw new IllegalArgumentException("Unsupported field type: " + type);
        };
    }

    private String datasourceSnippet(Service service) {
        Database database = service.getDatabase();
        if (database == null || database.getType() == DatabaseType.NONE) {
            return "";
        }
        if (service.getPersistenceStyle() == PersistenceStyle.JDBC_TEMPLATE) {
            if (database.getType() == DatabaseType.POSTGRES) {
                return "  datasource:\n    url: jdbc:postgresql://localhost:5432/${spring.application.name}\n    username: postgres\n    password: postgres\n  sql:\n    init:\n      mode: always\n";
            }
            return "  datasource:\n    url: jdbc:h2:mem:${spring.application.name};DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE\n    driverClassName: org.h2.Driver\n    username: sa\n    password: \n  h2:\n    console:\n      enabled: true\n  sql:\n    init:\n      mode: always\n";
        }
        if (database.getType() == DatabaseType.POSTGRES) {
            return "  datasource:\n    url: jdbc:postgresql://localhost:5432/${spring.application.name}\n    username: postgres\n    password: postgres\n  jpa:\n    hibernate:\n      ddl-auto: update\n";
        }
        return "  datasource:\n    url: jdbc:h2:mem:${spring.application.name};DB_CLOSE_DELAY=-1\n    driverClassName: org.h2.Driver\n    username: sa\n    password: \n  h2:\n    console:\n      enabled: true\n  jpa:\n    hibernate:\n      ddl-auto: update\n";
    }

    private List<Field> normalizeFields(List<Field> fields) {
        List<Field> normalized = new ArrayList<>();
        boolean hasId = false;
        for (Field field : fields) {
            if ("id".equalsIgnoreCase(field.getName())) {
                hasId = true;
            }
            normalized.add(field);
        }
        if (!hasId) {
            normalized.add(new Field("id", "long"));
        }
        normalized.sort(Comparator.comparing(Field::getName, String.CASE_INSENSITIVE_ORDER));
        return normalized;
    }

    private List<Map<String, Object>> nonIdFields(List<Field> fields) {
        return mapFields(fields).stream()
                .filter(field -> !"id".equalsIgnoreCase((String) field.get("name")))
                .toList();
    }

    private String selectColumns(List<Field> fields) {
        return mapFields(fields).stream()
                .map(field -> (String) field.get("name"))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .reduce((left, right) -> left + ", " + right)
                .orElse("id");
    }

    private String insertColumns(List<Field> fields) {
        return nonIdFields(fields).stream()
                .map(field -> (String) field.get("name"))
                .reduce((left, right) -> left + ", " + right)
                .orElse("");
    }

    private String insertPlaceholders(List<Field> fields) {
        int size = nonIdFields(fields).size();
        if (size == 0) {
            return "";
        }
        return "?,".repeat(size).replaceAll(",$", "");
    }

    private List<Map<String, Object>> updatableFields(List<Field> fields) {
        return nonIdFields(fields);
    }

    private String buildSchemaSql(List<Entity> entities) {
        StringBuilder schema = new StringBuilder();
        entities.stream()
                .sorted(Comparator.comparing(Entity::getName))
                .forEach(entity -> {
                    List<Map<String, Object>> fields = mapFields(normalizeFields(entity.getFields()));
                    schema.append("CREATE TABLE IF NOT EXISTS ")
                            .append(pluralize(decapitalize(entity.getName())))
                            .append(" (\n");
                    for (int index = 0; index < fields.size(); index++) {
                        Map<String, Object> field = fields.get(index);
                        String name = (String) field.get("name");
                        String sqlType = (String) field.get("sqlType");
                        schema.append("    ")
                                .append(name)
                                .append(" ")
                                .append("id".equalsIgnoreCase(name) ? sqlType + " GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY" : sqlType + " NOT NULL");
                        if (index < fields.size() - 1) {
                            schema.append(",");
                        }
                        schema.append("\n");
                    }
                    schema.append(");\n\n");
                });
        return schema.toString();
    }

    private String sanitizePackageSegment(String value) {
        return value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    private String decapitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return Character.toLowerCase(input.charAt(0)) + input.substring(1);
    }

    private String capitalize(String input) {
        if (input == null || input.isBlank()) {
            return input;
        }
        return Character.toUpperCase(input.charAt(0)) + input.substring(1);
    }

    private String pluralize(String singular) {
        if (singular.endsWith("s")) {
            return singular;
        }
        return singular + "s";
    }
}
