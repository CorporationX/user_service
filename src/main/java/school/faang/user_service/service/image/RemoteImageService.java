package school.faang.user_service.service.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.RemoteImageClient;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class RemoteImageService {

    private final RemoteImageClient remoteImageClient;

    public ResponseEntity<byte[]> getUserProfileImageFromRemoteService() throws IOException {
        log.info("Trying to get random profile picture");

        try {
            log.info("Successfully got response");
            return remoteImageClient.getUserProfileImage();
        } catch (Exception e) {
            log.error("Failed to fetch image", e);
            throw new IOException("Failed to fetch image");
        }
    }
}
