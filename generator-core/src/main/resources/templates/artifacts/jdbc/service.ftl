package ${packageName}.service;

import ${packageName}.entity.${entityName};
import ${packageName}.repository.${entityName}Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ${entityName}Service {
    private final ${entityName}Repository repository;

    public ${entityName}Service(${entityName}Repository repository) {
        this.repository = repository;
    }

    public List<${entityName}> findAll() {
        return repository.findAll();
    }

    public Optional<${entityName}> findById(Long id) {
        return repository.findById(id);
    }

    public ${entityName} create(${entityName} entity) {
        entity.setId(null);
        return repository.save(entity);
    }

    public Optional<${entityName}> update(Long id, ${entityName} entity) {
        if (!repository.existsById(id)) {
            return Optional.empty();
        }
        entity.setId(id);
        return Optional.of(repository.save(entity));
    }

    public boolean delete(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }
}
