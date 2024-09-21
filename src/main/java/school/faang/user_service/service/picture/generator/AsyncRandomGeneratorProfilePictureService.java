package school.faang.user_service.service.picture.generator;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.dice.bear.DiceBearClient;
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

    private final ExecutorService executor = Executors.newFixedThreadPool(12);
    private List<byte[]> defaultProfilePictures;

    @Value("${profile-picture.normal.size}")
    private int size;

    @Value("${profile-picture.small.small-size}")
    private int smallSize;

    @Value("${profile-picture.normal.path}")
    private String path;

    @Value("${profile-picture.small.path}")
    private String smallPath;

    @Override
    public List<byte[]> getProfilePictures(String seed) {
        try {
            log.info("Get avatars by seed {}.", seed);

            var picFuture = CompletableFuture.supplyAsync(
                    () -> diceBearClient.getSvgAvatar(seed, size), executor);
            var smallPicFuture = CompletableFuture.supplyAsync(
                    () -> diceBearClient.getSvgAvatar(seed, smallSize), executor);

            return List.of(picFuture.join(), smallPicFuture.join());

        } catch (Exception exception) {
            log.error("Failed to get avatar for seed {}.", seed, exception);
            return defaultProfilePictures;
        }
    }

    @PostConstruct
    public void initializeDefaultPictures() {
        defaultProfilePictures = List.of(
                profilePictureReader.readFile(path),
                profilePictureReader.readFile(smallPath)
        );
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