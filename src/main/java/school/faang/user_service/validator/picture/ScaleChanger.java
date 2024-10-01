package school.faang.user_service.validator.picture;

import org.imgscalr.Scalr;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ScaleChanger {

    public List<ResponseEntity<byte[]>> changeFileScale(MultipartFile file) {
        List<ResponseEntity<byte[]>> images = new ArrayList<>();
        images.add(changeScale(file, 1080));
        images.add(changeScale(file, 170));

        return images;
    }

    private ResponseEntity<byte[]> changeScale(MultipartFile file, int scale) {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            BufferedImage image = Scalr.resize(ImageIO.read(
                            file.getInputStream()),
                    Scalr.Method.QUALITY,
                    scale,
                    scale);

            ImageIO.write(image, "jpg", stream);
            byte[] result = stream.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentLength(result.length);

            return new ResponseEntity<>(result, headers, HttpStatus.OK);
        } catch (IOException e) {
            throw new RuntimeException("Failed to changed file size", e.getCause());
        }
    }
}
