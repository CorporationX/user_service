package school.faang.user_service.service.avatar;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.entity.UserProfilePic;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class ProfilePicServiceImpl {
    @Value("${randomAvatar.url}")
    private String url;
    @Value("${randomAvatar.styles}")
    private String[] styles;
    private RestTemplate restTemplate = new RestTemplate();

    public UserProfilePic generatePic(){
        byte[] image = restTemplate.getForObject(url + styles[ThreadLocalRandom.current().nextInt(styles.length)] + "/svg", byte[].class);
        return null;
    }
}
