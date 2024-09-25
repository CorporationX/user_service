package school.faang.user_service.service.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class AvatarApiService {
    private final String BASE_URL = "https://api.dicebear.com/9.x";
    private final String AVATAR_TYPE = "initials";
    private final String FILE_TYPE = "svg";
    private final String BASE_PARAMS = "scale=50&backgroundType=gradientLinear&radius=50";
    private final RestTemplate restTemplate;

    public byte[] getDefaultAvatar(String username) {
        String url = String.format("%s/%s/%s?%s&seed=%s", BASE_URL, AVATAR_TYPE, FILE_TYPE, BASE_PARAMS, username);
        return restTemplate.getForObject(url, byte[].class);
    }
}
