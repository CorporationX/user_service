package school.faang.user_service.service.avatar;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.avatar.UserProfilePicDto;

public interface ProfilePicService {

    UserProfilePicDto saveProfilePic(long userId, MultipartFile file);

    InputStreamResource getProfilePic(long userId);

    String deleteProfilePic(long userId);
}
