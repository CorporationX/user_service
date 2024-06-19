package school.faang.user_service.service.user.image;

import ij.ImagePlus;
import ij.io.Opener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.DataGettingException;

import java.awt.image.BufferedImage;
import java.util.Random;

import static school.faang.user_service.exception.message.ExceptionMessage.RANDOM_AVATAR_GETTING_EXCEPTION;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarGeneratorService {
    @Value("${services.avatar-generator.baseUrl}")
    private String baseUrl;

    public BufferedImage getRandomAvatar() {
        String url = baseUrl + new Random().nextLong();

        ImagePlus ipImage = new Opener().openURL(url);

        if (ipImage == null) {
            log.error("Returned avatar object is null.");
            throw new DataGettingException(RANDOM_AVATAR_GETTING_EXCEPTION.getMessage());
        }

        log.info("Avatar generated successfully.");
        return ipImage.getBufferedImage();
    }
}
