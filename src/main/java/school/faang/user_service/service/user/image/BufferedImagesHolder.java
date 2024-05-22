package school.faang.user_service.service.user.image;

import lombok.Getter;
import org.imgscalr.Scalr;

import java.awt.image.BufferedImage;

@Getter
public class BufferedImagesHolder {
    private static final int BIG_PIC_MAX_DIM = 1080;
    private static final int SMALL_PIC_MAX_DIM = 170;
    private BufferedImage bigPicture;
    private BufferedImage smallPicture;

    public BufferedImagesHolder(BufferedImage originalPic) {
        int originalWidth = originalPic.getWidth();
        int originalHeight = originalPic.getHeight();

        boolean maxIsWidth = originalWidth > originalHeight;
        if (maxIsWidth) {

            setPics(originalPic, originalWidth);
        } else {
            setPics(originalPic, originalHeight);
        }
    }

    private void setPics(BufferedImage originalPic, int biggerDimension) {
        int bigPicBiggerDimension = Math.min(biggerDimension, BIG_PIC_MAX_DIM);
        bigPicture = Scalr.resize(originalPic, bigPicBiggerDimension);

        int smallPicBiggerDimension = Math.min(biggerDimension, SMALL_PIC_MAX_DIM);
        smallPicture = Scalr.resize(originalPic, smallPicBiggerDimension);
    }
}
