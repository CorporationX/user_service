package school.faang.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "diceBearClient", url = "${dicebear.api.url}", configuration = DiceBearConfig.class)
public interface DiceBearClient {
    @GetMapping("/{style}/{seed}.svg")
    byte[] generateAvatar(@PathVariable("style") String style, @PathVariable("seed") String seed);
}