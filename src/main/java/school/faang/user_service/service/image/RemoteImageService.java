package school.faang.user_service.service.image;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.ImageGeneratorClient;
import school.faang.user_service.exception.remote.ImageGeneratorException;

@Slf4j
@Service
@RequiredArgsConstructor
public class RemoteImageService {

    private final ImageGeneratorClient imageGeneratorClient;

    public ResponseEntity<byte[]> getUserProfileImageFromRemoteService() {
        log.info("Trying to get random picture");

        try {
            ResponseEntity<byte[]> response = imageGeneratorClient.getUserProfileImage();
            log.info("Successfully got response");
            return response;
        } catch (FeignException e) {
            log.error("Failed to fetch image", e);
            throw new ImageGeneratorException("Failed to fetch image");
        }
    }
}
