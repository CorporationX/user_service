package school.faang.user_service.validator.picture;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

@Component
@NoArgsConstructor
public class PictureValidator {

    @Value("${servlet.multipart.max-file-size}")
    private long maxFileSize;

    @Autowired
    public PictureValidator(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public void checkPictureSizeExceeded(MultipartFile file) {
        if (file.getSize() > maxFileSize) {
            throw new MaxUploadSizeExceededException(maxFileSize);
        }
    }
}
