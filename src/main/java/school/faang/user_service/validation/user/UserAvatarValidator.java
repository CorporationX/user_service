package school.faang.user_service.validation.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;

@Component
@RequiredArgsConstructor
public class UserAvatarValidator {

    public void validateIfAvatarIsImage(String contentType) {
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new DataValidationException("Avatar must be an image");
        }
    }
}
