package school.faang.user_service.service.picture.generator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import school.faang.user_service.client.dice.bear.DiceBearClient;
import school.faang.user_service.config.s3.ProfilePictureProperties;
import school.faang.user_service.util.BinaryFileReader;

import java.util.List;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class AsyncRandomGeneratorProfilePictureServiceTest {

    @Mock
    private DiceBearClient diceBearClient;

    @Mock
    private BinaryFileReader profilePictureReader;

    @Spy
    private ProfilePictureProperties properties;

    @InjectMocks
    private AsyncRandomGeneratorProfilePictureService service;

    private final String seed = "seed";
    private final byte[] picture = {7, 8, 9};
    private final byte[] smallPicture = {10, 11, 12};
    private final byte[] defaultProfilePicture = {1, 2, 3};
    private final byte[] defaultSmallProfilePicture = {4, 5, 6};
    private final int size = 320;
    private final int smallSize = 120;

    @BeforeEach
    void setUp() {
        String path = "path";
        String smallPath = "small path";

        when(profilePictureReader.readFile(path)).thenReturn(defaultProfilePicture);
        when(profilePictureReader.readFile(smallPath)).thenReturn(defaultSmallProfilePicture);

        properties.setNormal(new ProfilePictureProperties.Picture(path, size));
        properties.setSmall(new ProfilePictureProperties.Picture(smallPath, smallSize));

        service.initializeDefaultPictures();
    }

    @Test
    void getProfilePictures_WhenOk() {
        List<byte[]> correctAnswer = List.of(picture, smallPicture);
        when(diceBearClient.getSvgAvatar(seed, size)).thenReturn(picture);
        when(diceBearClient.getSvgAvatar(seed, smallSize)).thenReturn(smallPicture);

        List<byte[]> result = service.getProfilePictures(seed);

        assertEquals(correctAnswer, result);
        verify(diceBearClient).getSvgAvatar(seed, size);
        verify(diceBearClient).getSvgAvatar(seed, smallSize);
    }

    @Test
    void getProfilePictures_WhenExceptionFromDiceBearClient() {
        List<byte[]> correctAnswer = List.of(defaultProfilePicture, defaultSmallProfilePicture);
        when(diceBearClient.getSvgAvatar(seed, size)).thenThrow(CompletionException.class);

        List<byte[]> result = service.getProfilePictures(seed);

        assertEquals(correctAnswer, result);
    }

    @Test
    void initializeDefaultPictures_ShouldLoadDefaultPictures() {
        List<byte[]> correctAnswer = List.of(defaultProfilePicture, defaultSmallProfilePicture);

        List<byte[]> result = service.getDefaultProfilePictures();

        assertEquals(correctAnswer, result);
    }

    @Test
    void shutdown() {
        var service = new AsyncRandomGeneratorProfilePictureService(diceBearClient, profilePictureReader, properties);

        service.shutdown();
        boolean isShutdown = service.getExecutor().isShutdown();

        assertTrue(isShutdown);
    }
}