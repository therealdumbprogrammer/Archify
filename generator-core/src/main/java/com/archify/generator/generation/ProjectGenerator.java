package com.archify.generator.generation;

import com.archify.generator.domain.Architecture;
import com.archify.generator.domain.Service;

import java.util.ArrayList;
import java.util.List;

public class ProjectGenerator {
    private final ServiceGenerator serviceGenerator;

    public ProjectGenerator(ServiceGenerator serviceGenerator) {
        this.serviceGenerator = serviceGenerator;
    }

    public List<FileLeaf> generate(Architecture architecture) {
        List<FileLeaf> files = new ArrayList<>();
        for (Service service : architecture.getServices()) {
            files.addAll(serviceGenerator.generate(service));
        }
        return files;
    }
}
