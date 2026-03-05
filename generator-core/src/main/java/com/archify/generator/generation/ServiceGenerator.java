package com.archify.generator.generation;

import com.archify.generator.domain.Database;
import com.archify.generator.domain.Entity;
import com.archify.generator.domain.Field;
import com.archify.generator.domain.Service;
import com.archify.generator.domain.ServiceCall;
import com.archify.generator.domain.enums.DatabaseType;
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

        files.add(new FileLeaf(serviceDir + "/pom.xml", templateEngine.render("artifacts/pom.ftl", baseModel)));
        files.add(new FileLeaf(serviceDir + "/src/main/java/" + packagePath + "/Application.java",
                templateEngine.render("artifacts/application.ftl", baseModel)));

        Map<String, Object> appModel = new HashMap<>(baseModel);
        appModel.put("springDatasource", datasourceSnippet(service.getDatabase()));
        files.add(new FileLeaf(serviceDir + "/src/main/resources/application.yml",
                templateEngine.render("artifacts/application-yml.ftl", appModel)));

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
                    Map<String, Object> entityModel = new HashMap<>(baseModel);
                    entityModel.put("entityName", entity.getName());
                    entityModel.put("entityVar", decapitalize(entity.getName()));
                    entityModel.put("entityPath", pluralize(decapitalize(entity.getName())));
                    entityModel.put("fields", mapFields(entity.getFields()));
                    entityModel.put("imports", requiredImports(entity.getFields()));

                    files.add(new FileLeaf(
                            serviceDir + "/src/main/java/" + packagePath + "/entity/" + entity.getName() + ".java",
                            templateEngine.render("artifacts/entity.ftl", entityModel)
                    ));
                    files.add(new FileLeaf(
                            serviceDir + "/src/main/java/" + packagePath + "/repository/" + entity.getName() + "Repository.java",
                            templateEngine.render("artifacts/repository.ftl", entityModel)
                    ));
                    files.add(new FileLeaf(
                            serviceDir + "/src/main/java/" + packagePath + "/service/" + entity.getName() + "Service.java",
                            templateEngine.render("artifacts/service.ftl", entityModel)
                    ));
                    files.add(new FileLeaf(
                            serviceDir + "/src/main/java/" + packagePath + "/controller/" + entity.getName() + "Controller.java",
                            templateEngine.render("artifacts/controller.ftl", entityModel)
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

    private String datasourceSnippet(Database database) {
        if (database == null || database.getType() == DatabaseType.NONE) {
            return "";
        }
        if (database.getType() == DatabaseType.POSTGRES) {
            return "  datasource:\n    url: jdbc:postgresql://localhost:5432/${spring.application.name}\n    username: postgres\n    password: postgres\n  jpa:\n    hibernate:\n      ddl-auto: update\n";
        }
        return "  datasource:\n    url: jdbc:h2:mem:${spring.application.name};DB_CLOSE_DELAY=-1\n    driverClassName: org.h2.Driver\n    username: sa\n    password: \n  h2:\n    console:\n      enabled: true\n  jpa:\n    hibernate:\n      ddl-auto: update\n";
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
