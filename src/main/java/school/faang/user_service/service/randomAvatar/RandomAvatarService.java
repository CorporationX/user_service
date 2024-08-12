package school.faang.user_service.service.randomAvatar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.exception.randomAvatar.AvatarGenerationException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class RandomAvatarService {
    @Value("${dicebear.avatars}")
    private  List<String> avatars;

    @Value("${dicebear.url}")
    private String url;

    @Value("${dicebear.type}")
    private String fileType;

    private final RestTemplate restTemplate;

    public File getRandomPhoto() {
        String avatarType = avatars.get(new Random().nextInt(avatars.size()));
        String uri = String.format("%s/%s/%s", url, avatarType.replaceAll(" ", "-").toLowerCase(), fileType);

        ResponseEntity<byte[]> response = restTemplate.exchange(uri, HttpMethod.GET, null, byte[].class);

        File file = new File("avatar");

        return writeResponseToFile(file, response);
    }

    public File writeResponseToFile(File file, ResponseEntity<byte[]> response) {
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            try (FileOutputStream fileOutputStream = new FileOutputStream(file)){
                fileOutputStream.write(response.getBody());
            } catch (IOException e) {
                log.warn(e.getMessage());
                throw new AvatarGenerationException(e.getMessage());
            }
        } else {
            throw new AvatarGenerationException("Avatar generated unsuccessfully");
        }

        return file;
    }
}
