package school.faang.user_service.service.avatar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import school.faang.user_service.client.DiceBearClient;
import school.faang.user_service.service.AvatarService;
import school.faang.user_service.service.S3Service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class AvatarServiceTest {

    private DiceBearClient diceBearClient;
    private S3Service s3Service;
    private AvatarService avatarService;

    @BeforeEach
    void setUp() {
        diceBearClient = Mockito.mock(DiceBearClient.class);
        s3Service = Mockito.mock(S3Service.class);
        avatarService = new AvatarService(diceBearClient, s3Service);
    }

    @Test
    void testGenerateAndSaveAvatar_Success() {
        String username = "testuser";
        String avatarUrl = "https://s3.amazonaws.com/bucket-name/avatars/testuser.svg";
        when(diceBearClient.generateAvatar(any(), eq(username))).thenReturn(new byte[]{});
        when(s3Service.uploadFile(any(), any())).thenReturn(avatarUrl);

        String result = avatarService.generateAndSaveAvatar(username);

        assertEquals(avatarUrl, result);
        verify(diceBearClient, times(1)).generateAvatar("adventurer", username);
        verify(s3Service, times(1)).uploadFile(any(), eq("avatars/testuser.svg"));
    }

    @Test
    void testGenerateAndSaveAvatar_Failure() {
        String username = "testuser";
        when(diceBearClient.generateAvatar(any(), eq(username))).thenThrow(new RuntimeException("Error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> avatarService.generateAndSaveAvatar(username));
        assertEquals("Failed to generate and save avatar for user: testuser", exception.getMessage());
    }
}
