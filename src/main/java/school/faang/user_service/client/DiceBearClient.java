package school.faang.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "diceBearClient", url = "${avatar_api.dice_bear.url}")
public interface DiceBearClient {

    @GetMapping(produces = "image/svg+xml")
    ResponseEntity<byte[]> generateAvatar(@RequestParam("seed") String seed);
}
