package school.faang.user_service.validator.userProfilePic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.exception.ExceptionMessages;

@Component
@Slf4j
public class UserProfilePicValidation {
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    public void validMaxSize(MultipartFile multipartFile){
       if (multipartFile.getSize() > MAX_FILE_SIZE){
           log.error(ExceptionMessages.PICTURE_LOADING_RESTRICTION);
           throw new MaxUploadSizeExceededException(MAX_FILE_SIZE);
       }
    }
}
