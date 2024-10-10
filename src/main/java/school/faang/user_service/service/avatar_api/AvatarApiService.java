package school.faang.user_service.service.avatar_api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.Objects;

@Service
public class AvatarApiService {

    private final String apiUrl;
    private final RestTemplate restTemplate;

    public AvatarApiService(@Value("${avatar_api.dice_bear.url}") String apiUrl, RestTemplate restTemplate) {
        if (apiUrl.isBlank()) {
            throw new IllegalArgumentException("API url for avatar generation can't be null/empty");
        }
        this.apiUrl = apiUrl;
        this.restTemplate = restTemplate;
    }

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
