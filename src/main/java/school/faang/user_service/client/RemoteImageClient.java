package school.faang.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "remote-image", url = "${external.apis.urls.remoteAvatars}")
public interface RemoteImageClient {

    @GetMapping("/${external.apis.paths.getRemoteAvatars}")
    ResponseEntity<byte[]> getUserProfileImage();
}
