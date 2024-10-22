package school.faang.user_service.service.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class UploadAvatarToS3 {
    private final S3Service s3Service;

    public void upload(BufferedImage image, String format, String filePath, Long userId, String contentType) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, format, os);
        byte[] imageBytes = os.toByteArray();

        s3Service.uploadFile(imageBytes, filePath, userId, contentType);
    }
}