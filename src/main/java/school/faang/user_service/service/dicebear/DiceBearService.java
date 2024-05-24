package school.faang.user_service.service.dicebear;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiceBearService {

    private final WebClient webClient;

    @Value("${urls.dicebear_avatar_service_url}")
    private String DICEBEAR_AVATAR_URL;

    public byte[] getAvatar(String userEmail) {
        return webClient
                .get()
                .uri(DICEBEAR_AVATAR_URL + userEmail)
                .retrieve()
                .bodyToMono(byte[].class)
                .block();
    }
}
