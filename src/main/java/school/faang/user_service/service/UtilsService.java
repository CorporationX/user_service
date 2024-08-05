package school.faang.user_service.service;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Component
public class UtilsService {

    public byte[] resizeImage(byte[] originalImageBytes, int width, int height, String extension) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(originalImageBytes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            Thumbnails.of(inputStream)
                    .size(width, height)
                    .outputFormat(extension)
                    .toOutputStream(outputStream);

            return outputStream.toByteArray();
        } catch (IOException e) {
            String errMessage = "Could not resize image avatar";
            log.error(errMessage, e);
            throw new RuntimeException(errMessage, e);
        }
    }
}
