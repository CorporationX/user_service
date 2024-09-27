package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserProfilePicDto;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.UserProfilePicService;

@Service
@RequiredArgsConstructor
public class UserProfilePicServiceImpl implements UserProfilePicService {

    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Override
    public void uploadAvatar(Long userId, MultipartFile file) {

    }

    @Override
    public UserProfilePicDto getAvatar(Long userId) {
        return null;
    }

    @Override
    public void deleteAvatar(Long userId) {

    }
}
