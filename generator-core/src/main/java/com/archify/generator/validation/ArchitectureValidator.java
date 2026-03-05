package com.archify.generator.validation;

import com.archify.generator.domain.Architecture;
import com.archify.generator.domain.Service;
import com.archify.generator.domain.ServiceCall;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ArchitectureValidator {
    public void validate(Architecture architecture) {
        validateUniqueServiceNames(architecture.getServices());
        validateCalls(architecture.getServices());
    }

    private void validateUniqueServiceNames(List<Service> services) {
        Set<String> names = new HashSet<>();
        for (Service service : services) {
            if (!names.add(service.getName())) {
                throw new IllegalArgumentException("Duplicate service name: " + service.getName());
            }
        }
    }

    private void validateCalls(List<Service> services) {
        Set<String> existing = services.stream().map(Service::getName).collect(Collectors.toSet());
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
}
