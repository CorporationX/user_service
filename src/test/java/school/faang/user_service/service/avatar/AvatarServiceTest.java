package school.faang.user_service.service.avatar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import school.faang.user_service.client.DiceBearClient;
import school.faang.user_service.service.AvatarService;
import school.faang.user_service.service.S3Service;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
        byte[] avatarBytes = new byte[]{};

        when(diceBearClient.generateAvatar(any(), eq(username))).thenReturn(avatarBytes);

        avatarService.generateAndSaveAvatar(username);

        verify(diceBearClient, times(1)).generateAvatar("adventurer", username);
        verify(s3Service, times(1)).uploadFile(eq(avatarBytes), eq("avatars/testuser.svg"));
    }

    @Test
    void testGenerateAndSaveAvatar_Failure() {
        String username = "testuser";
        when(diceBearClient.generateAvatar(any(), eq(username))).thenThrow(new RuntimeException("Error"));

        assertThrows(RuntimeException.class, () -> avatarService.generateAndSaveAvatar(username));
        verify(diceBearClient, times(1)).generateAvatar("adventurer", username);
        verifyNoInteractions(s3Service);
    }
}