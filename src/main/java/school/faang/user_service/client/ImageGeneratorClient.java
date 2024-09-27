package school.faang.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "remote-image", url = "${services.external.imagegenerator.endpoint}")
public interface ImageGeneratorClient {

    @GetMapping("/${services.external.imagegenerator.paths.get}")
    ResponseEntity<byte[]> getUserProfileImage();
}
