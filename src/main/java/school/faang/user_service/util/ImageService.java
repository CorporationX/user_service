package school.faang.user_service.util;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class ImageService {

    public InputStream resizeImage(MultipartFile file, boolean isBigImage) {
        try {
            BufferedImage originImage = ImageIO.read(file.getInputStream());
            int width = originImage.getWidth();
            int height = originImage.getHeight();
            changeSize(originImage, width, height, isBigImage);

            Image resizedImage = originImage
                    .getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            scaledImage.getGraphics().drawImage(resizedImage, 0, 0, null);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(scaledImage, "jpg", baos);

            return new ByteArrayInputStream(baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Resize error: " + e.getMessage());
        }
    }

    public void changeSize(BufferedImage bufferedImage, int width, int height, boolean isBigImage) {
        int pixels;

        if (isBigImage) {
            pixels = 1080;
        } else {
            pixels = 170;
        }

        double scale;
        if (bufferedImage.getHeight() > bufferedImage.getWidth()) {
            scale = (double) bufferedImage.getHeight() / pixels;
        } else {
            scale = (double) bufferedImage.getWidth() / pixels;
        }

        height = (int) (bufferedImage.getHeight() / scale);
        width = (int) (bufferedImage.getWidth() / scale);
    }
}
