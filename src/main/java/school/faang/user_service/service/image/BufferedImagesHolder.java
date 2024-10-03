package school.faang.user_service.service.image;

import lombok.Getter;
import org.imgscalr.Scalr;

import java.awt.image.BufferedImage;

@Getter
public class BufferedImagesHolder {
    private static final int BIG_PIC_MAX_DIM = 1080;
    private static final int SMALL_PIC_MAX_DIM = 170;
    private BufferedImage bigPic;
    private BufferedImage smallPic;

    public BufferedImagesHolder(BufferedImage originalPic) {
        int originalWidth = originalPic.getWidth();
        int originalHeight = originalPic.getHeight();
        int maxDimension = Math.max(originalWidth, originalHeight);
        setPics(originalPic, maxDimension);
    }

    private void setPics(BufferedImage originalPic, int biggerDimension) {
        int bigPicBiggerDimension = Math.min(biggerDimension, BIG_PIC_MAX_DIM);
        bigPic = Scalr.resize(originalPic, bigPicBiggerDimension);

        int smallPicBiggerDimension = Math.min(biggerDimension, SMALL_PIC_MAX_DIM);
        smallPic = Scalr.resize(originalPic, smallPicBiggerDimension);
    }
}