package com.archify.backend.api;

import com.archify.backend.dto.GenerateRequest;
import com.archify.backend.service.GenerationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/generate")
public class GenerationController {
    private final GenerationService generationService;
    private final Validator validator;
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    public GenerationController(GenerationService generationService, Validator validator) {
        this.generationService = generationService;
        this.validator = validator;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/zip")
    public ResponseEntity<byte[]> generateJson(@Valid @RequestBody GenerateRequest request) {
        return generateZip(request);
    }

    @PostMapping(consumes = {"application/yaml", "text/yaml", "application/x-yaml"}, produces = "application/zip")
    public ResponseEntity<byte[]> generateYaml(@RequestBody String yamlBody) {
        try {
            GenerateRequest request = yamlMapper.readValue(yamlBody, GenerateRequest.class);
            validateRequest(request);
            return generateZip(request);
        } catch (IllegalArgumentException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalArgumentException("Invalid YAML generate request: " + exception.getMessage(), exception);
        }
    }

    private void validateRequest(GenerateRequest request) {
        var violations = validator.validate(request);
        if (!violations.isEmpty()) {
            var violation = violations.iterator().next();
            String message = violation.getPropertyPath() + " " + violation.getMessage();
            throw new IllegalArgumentException(message);
        }
    }

    private ResponseEntity<byte[]> generateZip(GenerateRequest request) {
        byte[] zip = generationService.generate(request.getRecipe(), request.getConfig());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/zip"));
        headers.setContentDisposition(ContentDisposition.attachment().filename("generated-project.zip").build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(zip);
    }
}
