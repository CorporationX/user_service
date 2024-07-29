package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import school.faang.user_service.config.DiceBearConfig;
import school.faang.user_service.exception.AvatarGenerationException;

import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class DiceBearAvatarGenerator implements AvatarGenerator {

    private final DiceBearConfig diceBearConfig;
    private final Random rand = new Random();
    private final RestTemplate restTemplate;

    public Resource generateByCode(long key) {
        try {
            String fullUrl = buildAvatarUrl(key);
            ResponseEntity<Resource> response = restTemplate.getForEntity(fullUrl, Resource.class);
            log.info("Generated avatar at {}: Status {}", fullUrl, response.getStatusCode());

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                log.error("Failed to generate avatar, received status: {}", response.getStatusCode());
                throw new AvatarGenerationException("Failed to generate avatar");
            }

            return response.getBody();
        } catch (Exception e) {
            log.error("Error occurred while generating avatar for key {}: {}", key, e.getMessage(), e);
            throw new AvatarGenerationException("Failed to generate avatar", e);
        }
    }

    private String buildAvatarUrl(long key) {
        List<String> styles = diceBearConfig.getStyles();
        String randomStyle = styles.get(rand.nextInt(styles.size()));
        return UriComponentsBuilder.fromHttpUrl(diceBearConfig.getUrl())
                .pathSegment(randomStyle, diceBearConfig.getType())
                .queryParam("seed", key)
                .toUriString();
    }
}
