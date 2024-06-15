package school.faang.user_service.service.avatar;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.avatar.UserProfilePicDto;
import school.faang.user_service.dto.user.UserDto;

public interface ProfilePicService {

    void generateAndSetPic(UserDto user);

    UserProfilePicDto saveProfilePic(long userId, MultipartFile file);

    InputStreamResource getProfilePic(long userId);

    UserProfilePicDto deleteProfilePic(long userId);
}
