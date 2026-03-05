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

    public ${entityName} save(${entityName} ${entityVar}) {
        return repository.save(${entityVar});
    }
}
