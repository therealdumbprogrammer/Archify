package ${packageName}.controller;

import ${packageName}.entity.${entityName};
import ${packageName}.service.${entityName}Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/${entityVar}")
public class ${entityName}Controller {
    private final ${entityName}Service service;

    public ${entityName}Controller(${entityName}Service service) {
        this.service = service;
    }

    @GetMapping
    public List<${entityName}> list() {
        return service.findAll();
    }

    @PostMapping
    public ${entityName} create(@RequestBody ${entityName} ${entityVar}) {
        return service.save(${entityVar});
    }
}
