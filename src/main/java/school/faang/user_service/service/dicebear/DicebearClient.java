package school.faang.user_service.service.dicebear;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "dicebear", url = "${services.dicebear.endpoint}")
public interface DicebearClient {

    @GetMapping(value = "/{style}/{format}", produces = "image/webp")
    byte[] getImage(
            @PathVariable("style") String style,
            @PathVariable("format") String format,
            @RequestParam("seed") String seed,
            @RequestParam("size") int size
    );
}

