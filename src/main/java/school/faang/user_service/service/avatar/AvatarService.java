package school.faang.user_service.service.avatar;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AvatarService {
    private final String AVATAR_API_URL = "https://api.dicebear.com/5.x/avataaars/svg";

    public String generateRandomAvatar() {
        String url = AVATAR_API_URL + "?seed=" + System.currentTimeMillis();
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, String.class);
    }
}
