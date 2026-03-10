# Archify Generated Project

Recipe: `rest-jdbc-template-postgres`

This project uses Spring Boot, `JdbcTemplate`, and PostgreSQL.

Services:
<#list serviceNames as serviceName>
- ${serviceName}
</#list>

Before running, create a PostgreSQL database matching `spring.application.name`.

Run with:

```bash
./mvnw spring-boot:run
```
