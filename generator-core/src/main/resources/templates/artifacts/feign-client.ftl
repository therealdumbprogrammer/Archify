package ${packageName}.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "${targetService}")
public interface ${targetClientName} {
    @GetMapping("${callPath}")
    Object getById(@PathVariable("id") Long id);
}
