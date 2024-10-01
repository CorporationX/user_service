package school.faang.user_service.validator.picture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

@Component
public class PictureValidator {

    private Long maxFileSize;

    public PictureValidator(@Value("${servlet.multipart.max-file-size}") Long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public void checkPictureSizeExceeded(MultipartFile file) {
        if (file.getSize() > maxFileSize) {
            throw new MaxUploadSizeExceededException(maxFileSize);
        }
    }
}
