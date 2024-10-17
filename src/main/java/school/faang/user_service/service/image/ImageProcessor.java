package school.faang.user_service.service.image;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.exception.DataValidationException;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class ImageProcessor {

    @Value("${image.format}")
    public String formatName;

    public BufferedImage getBufferedImage(MultipartFile file) {
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        if (bufferedImage == null) {
            throw new DataValidationException("File is not image");
        }
        return bufferedImage;
    }

    public ByteArrayOutputStream getImageOs(Image image) {
        try (ByteArrayOutputStream bigImageOs = new ByteArrayOutputStream()) {
            ImageIO.write((RenderedImage) image, formatName, bigImageOs);
            return bigImageOs;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public BufferedImagesHolder scaleImage(BufferedImage originalPicture) {
        return new BufferedImagesHolder(originalPicture);
    }
}