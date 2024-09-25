package school.faang.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "remote-image", url = "${services.imagegenerator.endpoint}")
public interface ImageGeneratorClient {

    @GetMapping("/${services.imagegenerator.paths.get}")
    ResponseEntity<byte[]> getUserProfileImage();
}
