package ${packageName}.service;

import ${packageName}.entity.${entityName};
import ${packageName}.repository.${entityName}Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ${entityName}Service {
    private final ${entityName}Repository repository;

    public ${entityName}Service(${entityName}Repository repository) {
        this.repository = repository;
    }

    public List<${entityName}> findAll() {
        return repository.findAll();
    }

    public ${entityName} findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("${entityName} not found: " + id));
    }

    public ${entityName} create(${entityName} entity) {
        return repository.save(entity);
    }

    public ${entityName} update(Long id, ${entityName} entity) {
        ${entityName} existing = findById(id);
<#list fields as field>
<#if field.name != "id">
        existing.set${field.name?cap_first}(entity.get${field.name?cap_first}());
</#if>
</#list>
        return repository.save(existing);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
