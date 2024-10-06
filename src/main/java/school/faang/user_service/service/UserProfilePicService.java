package school.faang.user_service.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserProfilePicService {

    void uploadAvatar(MultipartFile file);
}
