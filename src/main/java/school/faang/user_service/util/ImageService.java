package school.faang.user_service.util;

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

//    public InputStream resizeImage(MultipartFile file, boolean isBigImage) {
//        try {
//            BufferedImage originImage = ImageIO.read(file.getInputStream());
//            int width = originImage.getWidth();
//            int height = originImage.getHeight();
//            changeSize(originImage, width, height, isBigImage);
//
//            Image resizedImage = originImage
//                    .getScaledInstance(width, height, Image.SCALE_SMOOTH);
//            BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//            scaledImage.getGraphics().drawImage(resizedImage, 0, 0, null);
//
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ImageIO.write(scaledImage, "jpg", baos);
//
//            return new ByteArrayInputStream(baos.toByteArray());
//        } catch (IOException e) {
//            throw new RuntimeException("Resize error: " + e.getMessage());
//        }
//    }

    public byte[] resize(MultipartFile file, boolean isBigImage) {
        try {
            InputStream fileInputStream = file.getInputStream();
            BufferedImage originImage = ImageIO.read(fileInputStream);

            int width = originImage.getWidth();
            int height = originImage.getHeight();
            int[] widthHeightArray = changeSize(originImage, width, height, isBigImage);

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

    public int[] changeSize(BufferedImage bufferedImage, int width, int height, boolean isBigImage) {

        int[] array = new int[2];
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

        array[0] = width;
        array[1] = height;

        return array;
    }
}
