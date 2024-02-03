package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.service.AmazonS3Service;

@Service
@RequiredArgsConstructor
public class UserProfilePicService {

    private final AmazonS3Service s3Service;
    private final UserService userService;

    @Transactional
    public void uploadProfilePic(MultipartFile file, long userId) {
        userService.getExistingUserById(userId);
        s3Service.uploadProfilePic(file, userId);
    }
}
