package com.archify.generator.generation;

import com.archify.generator.domain.Architecture;
import com.archify.generator.domain.Service;
import com.archify.generator.template.TemplateEngine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectGenerator {
    private static final String GENERATED_ROOT = "archify-project";

    private final ServiceGenerator serviceGenerator;
    private final TemplateEngine templateEngine;

    public ProjectGenerator(ServiceGenerator serviceGenerator, TemplateEngine templateEngine) {
        this.serviceGenerator = serviceGenerator;
        this.templateEngine = templateEngine;
    }

    public List<FileLeaf> generate(String recipeName, Architecture architecture) {
        List<FileLeaf> files = new ArrayList<>();
        files.add(new FileLeaf(
                GENERATED_ROOT + "/README.md",
                templateEngine.render("recipes/" + recipeName + "/README.ftl", readmeModel(architecture))
        ));

        architecture.getServices().stream()
                .sorted(Comparator.comparing(Service::getName))
                .forEach(service -> files.addAll(serviceGenerator.generate(GENERATED_ROOT, service)));

        return files;
    }

    private Map<String, Object> readmeModel(Architecture architecture) {
        List<String> serviceNames = architecture.getServices().stream()
                .map(Service::getName)
                .sorted()
                .toList();

        Map<String, Object> model = new HashMap<>();
        model.put("serviceNames", serviceNames);
        return model;
    }
}
