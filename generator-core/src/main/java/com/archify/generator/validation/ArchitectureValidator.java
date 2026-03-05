package com.archify.generator.validation;

import com.archify.generator.domain.Architecture;
import com.archify.generator.domain.Entity;
import com.archify.generator.domain.Field;
import com.archify.generator.domain.Service;
import com.archify.generator.domain.ServiceCall;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class ArchitectureValidator {
    private static final Set<String> SUPPORTED_FIELD_TYPES = Set.of(
            "string", "long", "integer", "boolean", "double", "localdate", "localdatetime", "bigdecimal"
    );

    public void validate(Architecture architecture) {
        validateUniqueServiceNames(architecture.getServices());
        validateCalls(architecture.getServices());
        validateEntities(architecture.getServices());
    }

    private void validateUniqueServiceNames(List<Service> services) {
        Set<String> names = new HashSet<>();
        for (Service service : services) {
            if (service.getName() == null || service.getName().isBlank()) {
                throw new IllegalArgumentException("Service name is required");
            }
            if (!names.add(service.getName().toLowerCase(Locale.ROOT))) {
                throw new IllegalArgumentException("Duplicate service name: " + service.getName());
            }
        }
    }

    private void validateCalls(List<Service> services) {
        Set<String> existing = services.stream()
                .map(Service::getName)
                .collect(Collectors.toSet());
        for (Service service : services) {
            for (ServiceCall call : service.getCalls()) {
                if (!existing.contains(call.getTargetService())) {
                    throw new IllegalArgumentException("Service call target does not exist: " + call.getTargetService());
                }
                if (call.getPath() == null || !call.getPath().startsWith("/")) {
                    throw new IllegalArgumentException("Service call path must start with '/': " + call.getPath());
                }
            }
        }
    }

    private void validateEntities(List<Service> services) {
        for (Service service : services) {
            if (service.getEntities() == null || service.getEntities().isEmpty()) {
                throw new IllegalArgumentException("Service must contain at least one entity: " + service.getName());
            }
            Set<String> entityNames = new HashSet<>();
            for (Entity entity : service.getEntities()) {
                if (entity.getName() == null || entity.getName().isBlank()) {
                    throw new IllegalArgumentException("Entity name is required");
                }
                if (!entityNames.add(entity.getName().toLowerCase(Locale.ROOT))) {
                    throw new IllegalArgumentException("Duplicate entity name in service " + service.getName() + ": " + entity.getName());
                }

                Set<String> fieldNames = new HashSet<>();
                for (Field field : entity.getFields()) {
                    if (field.getName() == null || field.getName().isBlank()) {
                        throw new IllegalArgumentException("Field name is required in entity: " + entity.getName());
                    }
                    if (field.getType() == null || field.getType().isBlank()) {
                        throw new IllegalArgumentException("Field type is required for field: " + field.getName());
                    }
                    if (!fieldNames.add(field.getName().toLowerCase(Locale.ROOT))) {
                        throw new IllegalArgumentException("Duplicate field name in entity " + entity.getName() + ": " + field.getName());
                    }

                    String normalizedType = field.getType().toLowerCase(Locale.ROOT);
                    if (!SUPPORTED_FIELD_TYPES.contains(normalizedType)) {
                        throw new IllegalArgumentException("Unsupported field type: " + field.getType());
                    }
                }
            }
        }
    }
}
