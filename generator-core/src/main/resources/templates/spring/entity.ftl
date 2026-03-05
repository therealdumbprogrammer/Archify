package ${packageName}.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ${entityName} {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
<#list fields as field>
<#if field.name != "id">
    private ${field.javaType} ${field.name};
</#if>
</#list>

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
<#list fields as field>
<#if field.name != "id">

    public ${field.javaType} get${field.name?cap_first}() {
        return ${field.name};
    }

    public void set${field.name?cap_first}(${field.javaType} ${field.name}) {
        this.${field.name} = ${field.name};
    }
</#if>
</#list>
}
