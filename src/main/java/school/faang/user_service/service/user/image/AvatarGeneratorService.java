package school.faang.user_service.service.user.image;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.exception.DataGettingException;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

import static school.faang.user_service.exception.ExceptionMessage.RANDOM_AVATAR_GETTING_EXCEPTION;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarGeneratorService {
    @Value("${services.avatar-generator.baseUrl}")
    private String baseUrl;
    private RestTemplate restTemplate;

    @PostConstruct
    public void setUp() {
        restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(List.of(new BufferedImageHttpMessageConverter()));
    }

    public BufferedImage getRandomAvatar() {
        String url = baseUrl + new Random().nextLong();

        BufferedImage responseBody = restTemplate.getForObject(url, BufferedImage.class);

        if (responseBody == null) {
            log.error("Returned avatar object is null.");
            throw new DataGettingException(RANDOM_AVATAR_GETTING_EXCEPTION.getMessage());
        }

        log.info("Avatar generated successfully.");
        return responseBody;
    }
}
