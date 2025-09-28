package school.faang.user_service.service.image;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {
    String uploadTeamAvatar(MultipartFile file, long teamId) throws IOException;
    void deleteTeamAvatar(String avatarKey);
    byte[] getTeamAvatar(String avatarKey);
}
