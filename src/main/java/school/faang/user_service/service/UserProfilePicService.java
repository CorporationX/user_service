package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.userProfilePic.UserProfilePicDto;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserProfilePicService {
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public UserProfilePicDto upload(MultipartFile file) {
        return null;
    }
}
