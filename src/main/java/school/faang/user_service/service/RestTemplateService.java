package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.exception.ImageFetchException;

@Service
@Slf4j
@RequiredArgsConstructor
public class RestTemplateService {

    private final RestTemplate restTemplate;

    public byte[] getImageBytes(String url) {
        try {
            return restTemplate.getForObject(url, byte[].class);
        } catch (RestClientException e) {
            log.error("Error calling API: {}", e.getMessage());
            throw new ImageFetchException("Failed to fetch image from URL: " + url);
        }
    }
}
