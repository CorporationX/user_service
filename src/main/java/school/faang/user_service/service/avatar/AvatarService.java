package school.faang.user_service.service.avatar;

import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
@RequiredArgsConstructor
public class AvatarService {
    private static final String AVATAR_API_URL = "https://api.dicebear.com/5.x/avataaars/svg";
    private static final String BUCKET_NAME = "faang-school";

    private final AmazonS3 s3Client;

    public String generateRandomAvatar(String userId) {
        String avatarSvg = createAvatar();
        return uploadAvatarToS3(avatarSvg, userId);
    }

    private String createAvatar() {
        String url = AVATAR_API_URL + "?seed=" + System.currentTimeMillis();
        RestTemplate restTemplate = new RestTemplate();
        try {
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String uploadAvatarToS3(String avatarSvg, String userId) {
        try {
            File tempFile = File.createTempFile("avatar-" + userId, ".svg");
            Files.writeString(tempFile.toPath(), avatarSvg);

            s3Client.putObject(BUCKET_NAME, "avatars/" + userId + ".svg", tempFile);
            tempFile.delete();

            return s3Client.getUrl(BUCKET_NAME, "avatars/" + userId + ".svg").toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
