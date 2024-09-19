package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserRegistrationDto;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class AsyncProfilePictureService implements AsyncSaveObject<UserRegistrationDto> {

    private final AmazonS3Service<UserRegistrationDto> s3Service;
    private final GeneratorPictureService profilePictureService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(20);

    @Override
    public String acceptSavingAndGetKey(UserRegistrationDto userRegistrationDto) {
        CompletableFuture.
    }
}