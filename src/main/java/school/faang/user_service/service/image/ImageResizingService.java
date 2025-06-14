package school.faang.user_service.service.image;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Service
public class ImageResizingService {

    public byte[] resizeImage(MultipartFile file, int maxSize) {
        try {
            BufferedImage original = ImageIO.read(file.getInputStream());
            if (original == null) {
                throw new IllegalArgumentException("Invalid image file");
            }

            int width = original.getWidth();
            int height = original.getHeight();

            double scale = Math.min((double) maxSize / width, (double) maxSize / height);
            if (scale >= 1.0) {
                return file.getBytes();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Thumbnails.of(original)
                    .scale(scale)
                    .outputFormat("jpeg")
                    .toOutputStream(baos);

            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to resize image", e);
        }
    }
}
