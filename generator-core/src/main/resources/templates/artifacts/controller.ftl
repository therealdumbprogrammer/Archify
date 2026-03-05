package ${packageName}.controller;

import ${packageName}.entity.${entityName};
import ${packageName}.service.${entityName}Service;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/${entityPath}")
public class ${entityName}Controller {
    private final ${entityName}Service service;

    public ${entityName}Controller(${entityName}Service service) {
        this.service = service;
    }

    @GetMapping
    public List<${entityName}> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ${entityName} get(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ${entityName} create(@Valid @RequestBody ${entityName} entity) {
        return service.create(entity);
    }

    @PutMapping("/{id}")
    public ${entityName} update(@PathVariable Long id, @Valid @RequestBody ${entityName} entity) {
        return service.update(id, entity);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
