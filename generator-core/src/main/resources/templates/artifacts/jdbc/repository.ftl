package ${packageName}.repository;

import ${packageName}.entity.${entityName};
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class ${entityName}Repository {

    private static final RowMapper<${entityName}> ${entityName?upper_case}_ROW_MAPPER = (rs, rowNum) -> {
        ${entityName} entity = new ${entityName}();
<#list fields as field>
        entity.set${field.name?cap_first}(rs.getObject("${field.name}", ${field.javaType}.class));
</#list>
        return entity;
    };

    private final JdbcTemplate jdbcTemplate;

    public ${entityName}Repository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<${entityName}> findAll() {
        return jdbcTemplate.query("SELECT ${selectColumns} FROM ${tableName} ORDER BY id", ${entityName?upper_case}_ROW_MAPPER);
    }

    public Optional<${entityName}> findById(Long id) {
        List<${entityName}> entities = jdbcTemplate.query(
                "SELECT ${selectColumns} FROM ${tableName} WHERE id = ?",
                ${entityName?upper_case}_ROW_MAPPER,
                id);
        return entities.stream().findFirst();
    }

    public ${entityName} save(${entityName} entity) {
        if (entity.getId() == null) {
            return insert(entity);
        }

        update(entity);
        return entity;
    }

    public boolean existsById(Long id) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM ${tableName} WHERE id = ?",
                Integer.class,
                id);
        return count != null && count > 0;
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM ${tableName} WHERE id = ?", id);
    }

    private ${entityName} insert(${entityName} entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
<#if nonIdFields?size == 0>
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO ${tableName} DEFAULT VALUES",
                    new String[]{"${generatedIdColumn}"});
<#else>
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO ${tableName} (${insertColumns}) VALUES (${insertPlaceholders})",
                    new String[]{"${generatedIdColumn}"});
<#list nonIdFields as field>
            ps.setObject(${field?index + 1}, entity.get${field.name?cap_first}());
</#list>
</#if>
            return ps;
        }, keyHolder);

        Number generatedId = (Number) Optional.ofNullable(keyHolder.getKeys())
                .map(keys -> keys.get("${generatedIdColumn}"))
                .orElseGet(keyHolder::getKey);
        entity.setId(generatedId.longValue());
        return entity;
    }

    private void update(${entityName} entity) {
        jdbcTemplate.update(
                "UPDATE ${tableName} SET <#list updatableFields as field>${field.name} = ?<#if field_has_next>, </#if></#list> WHERE id = ?",
<#list updatableFields as field>
                entity.get${field.name?cap_first}(),
</#list>
                entity.getId());
    }
}
