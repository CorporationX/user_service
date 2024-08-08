package school.faang.user_service.service.image;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.exception.ImageFetchException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @InjectMocks
    private ImageService imageService;

    @Mock
    private RestTemplate restTemplate;

    private String url;
    private int newWidth;
    private int newHeight;
    private byte[] imageBytes;
    private byte[] originalImageBytes;
    private byte[] resizedImageBytes;


    @BeforeEach
    void setUp() {
        url = "https://example.com/image.png";
        newWidth = 50;
        newHeight = 50;
        imageBytes = new byte[]{};
        originalImageBytes = createTestImage(100, 100, "png");
        resizedImageBytes = imageService.resizeImage(originalImageBytes, 50, 50, "png");
    }

    @Test
    @DisplayName("Fetch image successfully")
    void testFetchImageSuccess() {
        when(restTemplate.getForObject(url, byte[].class)).thenReturn(imageBytes);

        byte[] result = imageService.fetchImage(url);

        assertEquals(imageBytes, result);
        verify(restTemplate).getForObject(url, byte[].class);
    }

    @Test
    @DisplayName("Fetch image throws exception")
    void testFetchImageFailure() {
        when(restTemplate.getForObject(url, byte[].class)).thenThrow(new RestClientException(""));

        assertThrows(ImageFetchException.class, () -> imageService.fetchImage(url));

        verify(restTemplate).getForObject(url, byte[].class);
    }

    @Test
    @DisplayName("Resize image successfully")
    void testResizeImageSuccess() {
        BufferedImage originalImage;
        try {
            originalImage = ImageIO.read(new ByteArrayInputStream(originalImageBytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BufferedImage resizedImage;
        try {
            resizedImage = ImageIO.read(new ByteArrayInputStream(resizedImageBytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(newWidth, resizedImage.getWidth());
        assertEquals(newHeight, resizedImage.getHeight());
        assertEquals(originalImage.getType(), resizedImage.getType());
    }

    private byte[] createTestImage(int width, int height, String format) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.RED);
        g.fillRect(0, 0, width, height);
        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, format, baos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return baos.toByteArray();
    }
}