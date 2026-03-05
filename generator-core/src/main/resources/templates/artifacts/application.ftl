package ${packageName};

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
<#if feignEnabled>
import org.springframework.cloud.openfeign.EnableFeignClients;
</#if>

@SpringBootApplication
<#if feignEnabled>
@EnableFeignClients
</#if>
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
