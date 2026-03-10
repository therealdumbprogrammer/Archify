package ${packageName}.entity;

<#list imports as importValue>
import ${importValue};
</#list>

public class ${entityName} {
<#list fields as field>
    private ${field.javaType} ${field.name};
</#list>

    public ${entityName}() {
    }

    public ${entityName}(<#list fields as field>${field.javaType} ${field.name}<#if field_has_next>, </#if></#list>) {
<#list fields as field>
        this.${field.name} = ${field.name};
</#list>
    }
<#list fields as field>

    public ${field.javaType} get${field.name?cap_first}() {
        return ${field.name};
    }

    public void set${field.name?cap_first}(${field.javaType} ${field.name}) {
        this.${field.name} = ${field.name};
    }
</#list>
}
