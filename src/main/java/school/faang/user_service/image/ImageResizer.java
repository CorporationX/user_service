package school.faang.user_service.image;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.exception.FileException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
public class ImageResizer {

    public MultipartFile resize(MultipartFile file, int width, int height) {
        BufferedImage originalImage;
        BufferedImage resizedImage;
        Graphics2D resizer;
        try {
            originalImage = ImageIO.read(file.getInputStream());
        } catch (IOException exception) {
            log.error(exception.getMessage(), exception);
            throw new FileException(exception.getMessage());
        }

        resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        resizer = resizedImage.createGraphics();
        resizer.drawImage(originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
        resizer.dispose();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] resizedImageInBytes;
        try {
            ImageIO.write(resizedImage, file.getContentType().split("/")[1], outputStream);
            resizedImageInBytes = outputStream.toByteArray();
            outputStream.flush();
            outputStream.close();
        } catch (IOException exception) {
            log.error(exception.getMessage(), exception);
            throw new FileException(exception.getMessage());
        }

        return newMultipartFile(file.getName(),
                file.getOriginalFilename(),
                file.getContentType(),
                resizedImageInBytes);
    }

    private MultipartFile newMultipartFile(@NotBlank String name,
                                           @NotBlank String originalFilename,
                                           @NotBlank String contentType,
                                           @NotNull @Size(min = 1) byte[] bytes) {
        return new MultipartFile() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getOriginalFilename() {
                return originalFilename;
            }

            @Override
            public String getContentType() {
                return contentType;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return bytes.length;
            }

            @Override
            public byte[] getBytes() {
                return bytes;
            }

            @Override
            public InputStream getInputStream() {
                return new ByteArrayInputStream(bytes);
            }

            @Override
            public void transferTo(File dest) {
            }
        };
    }
}
