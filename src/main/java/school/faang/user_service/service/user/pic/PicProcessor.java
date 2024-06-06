package school.faang.user_service.service.user.pic;

import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Component
public class PicProcessor {

    public ByteArrayOutputStream getPicBaosByLength(MultipartFile pic, int maxSizeLength) {
        ByteArrayOutputStream picBaos = new ByteArrayOutputStream();
        try {
            log.info("Началось сжатие картинки " + pic.getOriginalFilename() +
                    ", до картинки с максимальной стороной: " + maxSizeLength);
            Thumbnails.of(pic.getInputStream())
                    .size(maxSizeLength, maxSizeLength)
                    .toOutputStream(picBaos);
        } catch (IOException e) {
            log.error("IOException: ", e);
            throw new RuntimeException(e);
        }
        log.info("Сжатие картинки " + pic.getOriginalFilename() + " завершено.");
        return picBaos;
    }

    public ObjectMetadata getPicMetaData(MultipartFile pic, ByteArrayOutputStream picBaos) {
        ObjectMetadata picMetadata = new ObjectMetadata();
        picMetadata.setContentLength(picBaos.size());
        picMetadata.setContentType(pic.getContentType());
        picMetadata.addUserMetadata("originalFileName", pic.getOriginalFilename());

        return picMetadata;
    }
}