package school.faang.user_service.service.avatar_api;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AvatarApiService {
    @Value("${avatar_api.dice_bear.url}")
    private String apiUrl;
    private final RestTemplate restTemplate;

    public byte[] generateDefaultAvatar(String username) {
        String url = apiUrl + username;
        try {
            byte[] data = restTemplate.getForObject(url, byte[].class);
            if (Objects.isNull(data) || data.length == 0) {
                throw new RuntimeException("Avatar data generated is null/empty");
            }
            return data;
        } catch(RestClientException e) {
            throw new RestClientException("A call to generate a default avatar has failed");
        }
    }
}
