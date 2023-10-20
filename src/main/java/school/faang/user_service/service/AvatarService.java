package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.service.s3.S3Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvatarService {

    private final S3Service s3Service;
    private final UserService userService;

    public ResponseEntity<Object> uploadAvatar(long userId, MultipartFile file) {
        return null;
    }

    public void getAvatar(long userId) {

    }

    public void getSmallAvatar(long userId) {

    }

    private Image readImageFromByteArray(byte[] imageBytes) {
        BufferedImage image;
        try (InputStream inputStream = new ByteArrayInputStream(imageBytes)) {
            image = ImageIO.read(inputStream);
        } catch (IOException e) {
            log.error("Bytes to Image convertation exception. ", e);
            throw new RuntimeException(e);
        }
        return image;
    }

    private Image compressImage(Image originalImage, int maxSideSize) {

        return null;
    }


}
