package school.faang.user_service.service.user;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.exception.DataValidationException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static school.faang.user_service.exception.ExceptionMessage.FILE_PROCESSING_EXCEPTION;
import static school.faang.user_service.exception.ExceptionMessage.PICTURE_TYPE_EXCEPTION;

@Component
public class ImageProcessor {
    private static final int BIG_PIC_MAX_DIM = 1080;
    private static final int SMALL_PIC_MAX_DIM = 170;

    public BufferedImage getBufferedImage(MultipartFile pic) {
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(pic.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(FILE_PROCESSING_EXCEPTION.getMessage() + e.getMessage());
        }

        if (bufferedImage == null) {
            throw new DataValidationException(PICTURE_TYPE_EXCEPTION.getMessage());
        }

        return bufferedImage;
    }

    /**
     * @param image original image
     * @return the image as ByteArrayOutputStream with in jpeg format
     */
    public ByteArrayOutputStream getImageOS(Image image) {
        try (ByteArrayOutputStream bigPicOS = new ByteArrayOutputStream()) {
            ImageIO.write((RenderedImage) image, "jpeg", bigPicOS);

            return bigPicOS;
        } catch (IOException e) {
            throw new RuntimeException(FILE_PROCESSING_EXCEPTION.getMessage() + e.getMessage());
        }
    }

    public List<BufferedImage> scaleImage(BufferedImage originalPic) {
        int originalWidth = originalPic.getWidth();
        int originalHeight = originalPic.getHeight();
        float originalRatio;

        int bigPicWidth;
        int bigPicHeight;

        int smallPicWidth;
        int smallPicHeight;

        boolean maxIsWidth = originalWidth > originalHeight;
        if (maxIsWidth) {
            originalRatio = (float) originalHeight / originalWidth;

            bigPicWidth = Math.min(originalWidth, BIG_PIC_MAX_DIM);
            bigPicHeight = Math.round(bigPicWidth * originalRatio);

            smallPicWidth = Math.min(originalWidth, SMALL_PIC_MAX_DIM);
            smallPicHeight = Math.round(smallPicWidth * originalRatio);
        } else {
            originalRatio = (float) originalWidth / originalHeight;

            bigPicHeight = Math.min(originalHeight, BIG_PIC_MAX_DIM);
            bigPicWidth = Math.round(bigPicHeight * originalRatio);

            smallPicHeight = Math.min(originalHeight, SMALL_PIC_MAX_DIM);
            smallPicWidth = Math.round(smallPicHeight * originalRatio);
        }

        BufferedImage bufferedBigPic = getScaledImage(originalPic, bigPicWidth, bigPicHeight);
        BufferedImage bufferedSmallPic = getScaledImage(originalPic, smallPicWidth, smallPicHeight);

        return List.of(bufferedBigPic, bufferedSmallPic);
    }

    private BufferedImage getScaledImage(BufferedImage originalPic, int bigPicWidth, int bigPicHeight) {
        Image bigPic = originalPic.getScaledInstance(bigPicWidth, bigPicHeight, Image.SCALE_DEFAULT);

        BufferedImage bufferedBigPic = new BufferedImage(bigPicWidth, bigPicHeight, BufferedImage.TYPE_INT_RGB);
        bufferedBigPic.getGraphics().drawImage(bigPic, 0, 0, null);

        return bufferedBigPic;
    }
}

