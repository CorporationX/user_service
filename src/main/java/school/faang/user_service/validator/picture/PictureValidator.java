package school.faang.user_service.validator.picture;

import lombok.Setter;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Setter
public class PictureValidator {

    @Value("${servlet.multipart.max-file-size}")
    private long maxFileSize;

    public void checkPictureSizeExceeded(MultipartFile file) {
        if (file.getSize() > maxFileSize) {
            throw new MaxUploadSizeExceededException(maxFileSize);
        }
    }

    public List<byte[]> changeFileScale(MultipartFile file) {
        List<byte[]> images = new ArrayList<>();
        images.add(changeScale(file, 1080));
        images.add(changeScale(file, 170));

        return images;
    }

    private byte[] changeScale(MultipartFile file, int scale) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try {
            BufferedImage image = Scalr.resize(ImageIO.read(
                            file.getInputStream()),
                    Scalr.Method.QUALITY,
                    scale,
                    scale);

            ImageIO.write(image, "jpg", stream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to changed file size", e.getCause());
        }

        return stream.toByteArray();
    }
}
