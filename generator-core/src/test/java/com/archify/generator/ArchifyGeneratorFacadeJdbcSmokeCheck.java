package com.archify.generator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class ArchifyGeneratorFacadeJdbcSmokeCheck {

    private ArchifyGeneratorFacadeJdbcSmokeCheck() {
    }

    public static void main(String[] args) throws Exception {
        ArchifyGeneratorFacade facade = new ArchifyGeneratorFacade();

        verifyH2(unzip(facade.generateZip("jdbc-template-h2", sampleConfig())));
        verifyPostgres(unzip(facade.generateZip("jdbc-template-postgres", sampleConfig())));
    }

    private static void verifyH2(Map<String, String> files) {
        String root = "archify-project/inventory-service/";
        require(files.containsKey(root + "pom.xml"), "Missing generated pom.xml");
        require(files.containsKey(root + "src/main/resources/schema.sql"), "Missing schema.sql");
        requireContains(files.get(root + "pom.xml"), "spring-boot-starter-jdbc");
        requireContains(files.get(root + "pom.xml"), "<artifactId>h2</artifactId>");
        requireContains(files.get(root + "src/main/resources/application.yml"), "jdbc:h2:mem:${spring.application.name}");
        requireContains(files.get(root + "src/main/java/com/archify/inventoryservice/repository/InvoiceRepository.java"), "JdbcTemplate");
        requireContains(files.get(root + "src/main/resources/schema.sql"), "CREATE TABLE IF NOT EXISTS invoices");
        requireContains(files.get(root + "src/main/resources/schema.sql"), "amount DECIMAL(19,2) NOT NULL");
        requireContains(files.get(root + "src/main/resources/schema.sql"), "active BOOLEAN NOT NULL");
        require(!files.get(root + "src/main/java/com/archify/inventoryservice/entity/Invoice.java").contains("Trade"),
                "Generated entity should not contain hardcoded Trade references");
    }

    private static void verifyPostgres(Map<String, String> files) {
        String root = "archify-project/inventory-service/";
        requireContains(files.get(root + "pom.xml"), "<artifactId>postgresql</artifactId>");
        requireContains(files.get(root + "src/main/resources/application.yml"), "jdbc:postgresql://localhost:5432/${spring.application.name}");
        requireContains(files.get("archify-project/README.md"), "jdbc-template-postgres");
    }

    private static Map<String, Object> sampleConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("serviceName", "inventory-service");
        config.put("entities", List.of(
                Map.of(
                        "name", "Invoice",
                        "fields", List.of(
                                Map.of("name", "amount", "type", "bigdecimal"),
                                Map.of("name", "active", "type", "boolean")
                        )
                )
        ));
        return config;
    }

    private static Map<String, String> unzip(byte[] content) throws IOException {
        Map<String, String> files = new HashMap<>();
        try (ZipInputStream inputStream = new ZipInputStream(new ByteArrayInputStream(content), StandardCharsets.UTF_8)) {
            ZipEntry entry;
            while ((entry = inputStream.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    files.put(entry.getName(), new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
                }
            }
        }
        return files;
    }

    private static void requireContains(String value, String expected) {
        require(value != null && value.contains(expected), "Expected to find '" + expected + "'");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
