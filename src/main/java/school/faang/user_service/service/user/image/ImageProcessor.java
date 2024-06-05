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

    public BufferedImage getBufferedImage(MultipartFile file) {
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            log.error(FILE_PROCESSING_EXCEPTION.getMessage() + e.getMessage());
            throw new RuntimeException(FILE_PROCESSING_EXCEPTION.getMessage() + e.getMessage());
        }

        //Если загруженный файл не является картинкой, то в объект bufferedImage не запишется ничего
        if (bufferedImage == null) {
            log.error(PICTURE_TYPE_EXCEPTION.getMessage() + "Received file: " + file.getOriginalFilename());
            throw new DataValidationException(PICTURE_TYPE_EXCEPTION.getMessage());
        }

        return bufferedImage;
    }

    /**
     * @param image original image
     * @return the image as ByteArrayOutputStream with in jpeg format
     */
    public ByteArrayOutputStream getImageOS(Image image) {
        try (ByteArrayOutputStream bigImageOS = new ByteArrayOutputStream()) {
            ImageIO.write((RenderedImage) image, FORMAT_NAME, bigImageOS);

            return bigImageOS;
        } catch (IOException e) {
            log.error(FILE_PROCESSING_EXCEPTION.getMessage() + e.getMessage());
            throw new RuntimeException(FILE_PROCESSING_EXCEPTION.getMessage() + e.getMessage());
        }
    }

    public BufferedImagesHolder scaleImage(BufferedImage originalPicture) {
        return new BufferedImagesHolder(originalPicture);
    }
}

