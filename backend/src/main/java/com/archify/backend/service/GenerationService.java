package com.archify.backend.service;

import com.archify.generator.ArchifyGeneratorFacade;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GenerationService {
    private final ArchifyGeneratorFacade generatorFacade = new ArchifyGeneratorFacade();

    public byte[] generate(String recipe, Map<String, Object> config) {
        return generatorFacade.generateZip(recipe, config);
    }
}
