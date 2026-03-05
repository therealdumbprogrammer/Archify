package ${packageName}.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
<#list imports as importValue>
import ${importValue};
</#list>

@Entity
public class ${entityName} {
<#list fields as field>
<#if field.name == "id">
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
</#if>
    private ${field.javaType} ${field.name};
</#list>
<#list fields as field>

    public ${field.javaType} get${field.name?cap_first}() {
        return ${field.name};
    }

    public void set${field.name?cap_first}(${field.javaType} ${field.name}) {
        this.${field.name} = ${field.name};
    }
</#list>
}
