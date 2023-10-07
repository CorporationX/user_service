package school.faang.user_service.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class ImageHandler {

    public byte[] resizePic(MultipartFile file, int maxSizePx) {
        try {
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(file.getBytes()));

            int newWidth = getSideSize(maxSizePx, originalImage.getWidth());
            int newHeight = getSideSize(maxSizePx, originalImage.getHeight());

            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
            g.dispose();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpg", outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int getSideSize(int maxSizePx, int sideSize) {
        if (sideSize > maxSizePx) {
            double scale = (double) maxSizePx / sideSize;
            return (int) (sideSize * scale);
        }
        return sideSize;
    }
}
