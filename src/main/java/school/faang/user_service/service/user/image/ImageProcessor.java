package school.faang.user_service.service.user.image;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.exception.DataValidationException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static school.faang.user_service.exception.ExceptionMessage.FILE_PROCESSING_EXCEPTION;
import static school.faang.user_service.exception.ExceptionMessage.PICTURE_TYPE_EXCEPTION;

@Slf4j
@Component
public class ImageProcessor {
    public static final String FORMAT_NAME = "jpeg";

    public BufferedImage getBufferedImage(MultipartFile pic) {
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(pic.getInputStream());
        } catch (IOException e) {
            log.error(FILE_PROCESSING_EXCEPTION.getMessage() + e.getMessage());
            throw new RuntimeException(FILE_PROCESSING_EXCEPTION.getMessage() + e.getMessage());
        }

        if (bufferedImage == null) {
            log.error(PICTURE_TYPE_EXCEPTION.getMessage() + "Received file: " + pic.getOriginalFilename());
            throw new DataValidationException(PICTURE_TYPE_EXCEPTION.getMessage());
        }

        return bufferedImage;
    }

    /**
     * @param image original image
     * @return the image as ByteArrayOutputStream with in jpeg format
     */
    public ByteArrayOutputStream getImageOS(Image image) {
        try (ByteArrayOutputStream bigPicOS = new ByteArrayOutputStream()) {
            ImageIO.write((RenderedImage) image, FORMAT_NAME, bigPicOS);

            return bigPicOS;
        } catch (IOException e) {
            log.error(FILE_PROCESSING_EXCEPTION.getMessage() + e.getMessage());
            throw new RuntimeException(FILE_PROCESSING_EXCEPTION.getMessage() + e.getMessage());
        }
    }

    public BufferedPicHolder scaleImage(BufferedImage originalPic) {
        return new BufferedPicHolder(originalPic);
    }
}

