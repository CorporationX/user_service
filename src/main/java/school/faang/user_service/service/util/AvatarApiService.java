package school.faang.user_service.service.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.execchain.RequestAbortedException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AvatarApiService {
    private final RestTemplate restTemplate;

    public Optional<byte[]> getDefaultAvatar(String username) {
        String url = "https://api.dicebear.com/9.x/initials/svg" +
                "scale=50&backgroundType=gradientLinear&radius=50&seed=" + username;

        try {
            byte[] data = restTemplate.getForObject(url, byte[].class);
            return Optional.ofNullable(data);
        } catch(RestClientException e) {
            return Optional.empty();
        }
    }
}
