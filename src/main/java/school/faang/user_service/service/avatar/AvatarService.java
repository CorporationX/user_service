package school.faang.user_service.service.avatar;


import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@Slf4j
@Setter
@Service
@RequiredArgsConstructor
public class AvatarService {
    private final RestTemplate restTemplate;
    @Value("${dicebear.api.url}")
    private String dicebearApiUrl;

    public String generateRandomAvatar() {
        String seed = UUID.randomUUID().toString();
        String url = UriComponentsBuilder.fromHttpUrl(dicebearApiUrl)
                .queryParam("seed", seed)
                .toUriString();
        String avatar = restTemplate.getForObject(url, String.class);
        if (avatar == null) {
            throw new IllegalStateException("Could not generate an avatar");
        }
        return avatar;
    }
}
