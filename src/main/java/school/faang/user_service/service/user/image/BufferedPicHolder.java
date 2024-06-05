package school.faang.user_service.service.user.image;

import lombok.Getter;

import java.awt.*;
import java.awt.image.BufferedImage;

@Getter
public class BufferedPicHolder {
    private static final int BIG_PIC_MAX_DIM = 1080;
    private static final int SMALL_PIC_MAX_DIM = 170;
    private BufferedImage bigPic;
    private BufferedImage smallPic;

    public BufferedPicHolder(BufferedImage originalPic) {
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

        bigPic = bufferedBigPic;
        smallPic = bufferedSmallPic;
    }

    private BufferedImage getScaledImage(BufferedImage originalPic, int bigPicWidth, int bigPicHeight) {
        Image bigPic = originalPic.getScaledInstance(bigPicWidth, bigPicHeight, Image.SCALE_DEFAULT);

        BufferedImage bufferedBigPic = new BufferedImage(bigPicWidth, bigPicHeight, BufferedImage.TYPE_INT_RGB);
        bufferedBigPic.getGraphics().drawImage(bigPic, 0, 0, null);

        return bufferedBigPic;
    }
}
