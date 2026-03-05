package com.archify.generator.generation;

import com.archify.generator.domain.Database;
import com.archify.generator.domain.Entity;
import com.archify.generator.domain.Field;
import com.archify.generator.domain.Service;
import com.archify.generator.domain.enums.DatabaseType;
import com.archify.generator.template.TemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;

public class ServiceGenerator {
    private static final String GENERATED_ROOT = "generated-project";

    private final TemplateEngine templateEngine;

    public ServiceGenerator(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public List<FileLeaf> generate(Service service) {
        List<FileLeaf> files = new ArrayList<>();
        String serviceDir = GENERATED_ROOT + "/" + service.getName();
        String packageName = "com.archify." + sanitizePackageSegment(service.getName());
        String packagePath = packageName.replace('.', '/');

        Map<String, Object> baseModel = new HashMap<>();
        baseModel.put("serviceName", service.getName());
        baseModel.put("packageName", packageName);
        baseModel.put("database", service.getDatabase() == null ? "NONE" : service.getDatabase().getType().name());

        files.add(new FileLeaf(serviceDir + "/pom.xml", templateEngine.render("pom.xml.ftl", baseModel)));
        files.add(new FileLeaf(serviceDir + "/src/main/java/" + packagePath + "/Application.java",
                templateEngine.render("mainApplication.ftl", baseModel)));

        if (!service.getEntities().isEmpty()) {
            Entity entity = service.getEntities().get(0);
            Map<String, Object> entityModel = new HashMap<>(baseModel);
            entityModel.put("entityName", entity.getName());
            entityModel.put("entityVar", decapitalize(entity.getName()));
            entityModel.put("fields", mapFields(entity.getFields()));

            files.add(new FileLeaf(serviceDir + "/src/main/java/" + packagePath + "/entity/" + entity.getName() + ".java",
                    templateEngine.render("entity.ftl", entityModel)));
            files.add(new FileLeaf(serviceDir + "/src/main/java/" + packagePath + "/repository/" + entity.getName() + "Repository.java",
                    templateEngine.render("repository.ftl", entityModel)));
            files.add(new FileLeaf(serviceDir + "/src/main/java/" + packagePath + "/service/" + entity.getName() + "Service.java",
                    templateEngine.render("service.ftl", entityModel)));
            files.add(new FileLeaf(serviceDir + "/src/main/java/" + packagePath + "/controller/" + entity.getName() + "Controller.java",
                    templateEngine.render("controller.ftl", entityModel)));
        }

        Map<String, Object> ymlModel = new HashMap<>(baseModel);
        ymlModel.put("springDatasource", datasourceSnippet(service.getDatabase()));
        files.add(new FileLeaf(serviceDir + "/src/main/resources/application.yml",
                templateEngine.render("application.yml.ftl", ymlModel)));

        return files;
    }

    private List<Map<String, Object>> mapFields(List<Field> fields) {
        List<Map<String, Object>> mapped = new ArrayList<>();
        for (Field field : fields) {
            Map<String, Object> value = new HashMap<>();
            value.put("name", field.getName());
            value.put("javaType", mapFieldType(field));
            mapped.add(value);
        }
        return mapped;
    }

    private String mapFieldType(Field field) {
        return switch (field.getType()) {
            case STRING -> "String";
            case LONG -> "Long";
            case INTEGER -> "Integer";
            case BOOLEAN -> "Boolean";
        };
    }

    private String datasourceSnippet(Database database) {
        if (database == null) {
            return "";
        }
        if (database.getType() == DatabaseType.POSTGRES) {
            return "  datasource:\n    url: jdbc:postgresql://localhost:5432/" +
                    "${spring.application.name}\n    username: postgres\n    password: postgres\n" +
                    "  jpa:\n    hibernate:\n      ddl-auto: update\n";
        }
        return "  datasource:\n    url: jdbc:h2:mem:${spring.application.name};DB_CLOSE_DELAY=-1\n" +
                "    driverClassName: org.h2.Driver\n    username: sa\n    password: \n" +
                "  h2:\n    console:\n      enabled: true\n" +
                "  jpa:\n    hibernate:\n      ddl-auto: update\n";
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
}
