package school.faang.user_service.client.dicebear;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "dicebearClient", url = "${dicebear.avatar.base-url}")
public interface DicebearClient {

    @GetMapping(value = "${dicebear.avatar.style-path}", produces = "image/svg+xml")
    byte[] getAvatar(@PathVariable("style") String style);
}
