package school.faang.user_service.service.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.exception.ImageFetchException;
import school.faang.user_service.exception.ImageProcessingException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService {
    private final RestTemplate restTemplate;

    public byte[] fetchImage(String url) {
        try {
            return restTemplate.getForObject(url, byte[].class);
        } catch (RestClientException e) {
            log.error("Failed to fetch image from URL: {}. Error: {}", url, e.getMessage(), e);
            throw new ImageFetchException("Failed to fetch image from URL: " + url);
        }
    }

    public byte[] resizeImage(byte[] image, int width, int height, String imageFormat) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(image);
            BufferedImage originalImage = ImageIO.read(inputStream);

            BufferedImage resizedImage = new BufferedImage(width, height, originalImage.getType());

            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
            g.dispose();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, imageFormat, outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("Failed to resize image to width: {} and height: {}. Error: {}", width, height, e.getMessage(), e);
            throw new ImageProcessingException("Failed to resize image.");
        }
    }
}
