package school.faang.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "DiceBear", url = "https://api.dicebear.com/9.x/fun-emoji")
public interface DiceBearClient {

    @GetMapping("/svg")
    byte[] getSvgAvatar(@RequestParam String seed);
}
