package school.faang.user_service.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.exception.AvatarFetchException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AvatarClient {

    private static final String FETCHING_AVATAR_LOG = "Fetching avatar data from Dicebear URL {}";
    private static final String AVATAR_FETCH_SUCCESS_LOG = "Successfully fetched avatar data from Dicebear.";
    private static final String AVATAR_FETCH_ERROR_LOG = "Failed to fetch avatar from Dicebear API at URL {}";

    private final RestTemplate restTemplate;

    public byte[] fetchAvatarData(String url) {
        log.info(FETCHING_AVATAR_LOG, url);
        ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            log.error(AVATAR_FETCH_ERROR_LOG, url);
            throw new AvatarFetchException(url);
        }
        log.info(AVATAR_FETCH_SUCCESS_LOG);
        return response.getBody();
    }
}
