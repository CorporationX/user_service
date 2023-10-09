package school.faang.user_service.util;

import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

@Component
public class ResizeImage {
    public BufferedImage resizeImage(BufferedImage originalImage, int maxSize) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        int newWidth, newHeight;
        if (originalWidth > originalHeight) {
            newWidth = maxSize;
            newHeight = (int) (originalHeight * ((float) maxSize / originalWidth));
        } else {
            newWidth = (int) (originalWidth * ((float) maxSize / originalHeight));
            newHeight = maxSize;
        }

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        resizedImage.createGraphics().drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        return resizedImage;
    }
}
