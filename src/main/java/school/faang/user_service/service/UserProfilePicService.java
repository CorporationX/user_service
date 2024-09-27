package school.faang.user_service.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserProfilePicDto;

public interface UserProfilePicService {

    public void uploadAvatar(Long userId, MultipartFile file);

    public UserProfilePicDto getAvatar(Long userId);

    public void deleteAvatar(Long userId);
}
