package school.faang.user_service.service.picture.generator;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.dice.bear.DiceBearClient;
import school.faang.user_service.config.s3.ProfilePictureProperties;
import school.faang.user_service.service.GeneratorPictureService;
import school.faang.user_service.util.BinaryFileReader;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Setter
@Getter
@RequiredArgsConstructor
public class AsyncRandomGeneratorProfilePictureService implements GeneratorPictureService {

    private final DiceBearClient diceBearClient;
    private final BinaryFileReader profilePictureReader;
    private final ProfilePictureProperties profilePictureProperties;

    private final ExecutorService executor = Executors.newFixedThreadPool(12);
    private List<byte[]> defaultProfilePictures;

    @PostConstruct
    public void initializeDefaultPictures() {
        defaultProfilePictures = List.of(
                profilePictureReader.readFile(profilePictureProperties.getNormal().getPath()),
                profilePictureReader.readFile(profilePictureProperties.getSmall().getPath())
        );
    }

    @Override
    public List<byte[]> getProfilePictures(String seed) {
        try {
            log.info("Get avatars by seed {}.", seed);

            var picFuture = CompletableFuture.supplyAsync(
                    () -> diceBearClient.getSvgAvatar(seed, profilePictureProperties.getNormal().getSize()), executor);
            var smallPicFuture = CompletableFuture.supplyAsync(
                    () -> diceBearClient.getSvgAvatar(seed, profilePictureProperties.getSmall().getSize()), executor);

            return List.of(picFuture.join(), smallPicFuture.join());

        } catch (Exception exception) {
            log.error("Failed to get avatar for seed {}.", seed, exception);
            return defaultProfilePictures;
        }
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}