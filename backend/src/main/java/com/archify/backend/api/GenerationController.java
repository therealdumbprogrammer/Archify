package com.archify.backend.api;

import com.archify.backend.dto.GenerateRequest;
import com.archify.backend.service.GenerationService;
import jakarta.validation.Valid;
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

    public GenerationController(GenerationService generationService) {
        this.generationService = generationService;
    }

    @PostMapping(produces = "application/zip")
    public ResponseEntity<byte[]> generate(@Valid @RequestBody GenerateRequest request) {
        byte[] zip = generationService.generate(request.getRecipe(), request.getConfig());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/zip"));
        headers.setContentDisposition(ContentDisposition.attachment().filename("generated-project.zip").build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(zip);
    }
}
