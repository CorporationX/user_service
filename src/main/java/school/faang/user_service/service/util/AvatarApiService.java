package school.faang.user_service.service.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.execchain.RequestAbortedException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AvatarApiService {
    private final String BASE_URL = "https://api.dicebear.com/9.x";
    private final String AVATAR_TYPE = "initials";
    private final String FILE_TYPE = "svg";
    private final String BASE_PARAMS = "scale=50&backgroundType=gradientLinear&radius=50";
    private final RestTemplate restTemplate;

    public Optional<byte[]> getDefaultAvatar(String username) {
        String url = String.format("%s/%s/%s?%s&seed=%s", BASE_URL, AVATAR_TYPE, FILE_TYPE, BASE_PARAMS, username);
        try {
            return Optional.ofNullable(restTemplate.getForObject(url, byte[].class));
        } catch(RestClientException e) {
            return Optional.empty();
        }
    }
}
