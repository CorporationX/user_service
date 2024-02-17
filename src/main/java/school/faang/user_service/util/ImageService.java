package school.faang.user_service.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class ImageService {

    @Value("${image.pixels.bigSize}")
    private int pixelsBigSize;
    @Value("${image.pixels.smallSize}")
    private int pixelsSmallSize;

    public byte[] resize(MultipartFile file, boolean isBigImage) {
        try {
            InputStream fileInputStream = file.getInputStream();
            BufferedImage originImage = ImageIO.read(fileInputStream);

            int[] widthHeightArray = changeSize(originImage, isBigImage);

            BufferedImage resizedImage = new BufferedImage(widthHeightArray[0], widthHeightArray[1], BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = resizedImage.createGraphics();
            g2.drawImage(originImage, 0, 0, widthHeightArray[0], widthHeightArray[1], null);
            g2.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpg", baos);
            return baos.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Resize error: " + e.getMessage());
        }
    }

    public int[] changeSize(BufferedImage bufferedImage, boolean isBigImage) {

        int[] array = new int[2];
        int pixels;

        if (isBigImage) {
            pixels = pixelsBigSize;
        } else {
            pixels = pixelsSmallSize;
        }

        double scale;
        if (bufferedImage.getHeight() > bufferedImage.getWidth()) {
            scale = (double) bufferedImage.getHeight() / pixels;
        } else {
            scale = (double) bufferedImage.getWidth() / pixels;
        }

        int height = (int) (bufferedImage.getHeight() / scale);
        int width = (int) (bufferedImage.getWidth() / scale);

        array[0] = width;
        array[1] = height;

        return array;
    }
}
