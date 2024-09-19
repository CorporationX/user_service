package school.faang.user_service.service.picture.generator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.DiceBearClient;
import school.faang.user_service.service.GeneratorPictureService;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class RandomGeneratorProfilePictureService implements GeneratorPictureService {

    private final DiceBearClient diceBearClient;
    private final byte[] defaultProfilePicture;

    @Value("${profile-picture.size}")
    private int size;

    @Value("${profile-picture.small-size}")
    private int smallSize;

    private final List<String> seeds = List.of(
            "Robert", "Jade"
    );

    @Override
    public byte[] generatePicture(String seed) {
        log.info("Get avatar by seed {}", seed);
        try {
            return diceBearClient.getSvgAvatar(seed, size);
        } catch (Exception e) {
            log.error(e.getMessage(), e.fillInStackTrace());
            return defaultProfilePicture;
        }
    }

    @Override
    public byte[] generateSmallPicture(String seed) {
        log.info("Get small avatar by seed {}", seed);
        try {
            return diceBearClient.getSvgAvatar(seed, smallSize);
        } catch (Exception e) {
            log.error(e.getMessage(), e.fillInStackTrace());
            return defaultProfilePicture;
        }
    }

    @Override
    public String getSeed() {
        int index = ThreadLocalRandom.current().nextInt(seeds.size());
        return seeds.get(index);
    }
}