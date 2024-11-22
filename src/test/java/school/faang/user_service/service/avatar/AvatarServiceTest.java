package school.faang.user_service.service.avatar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvatarServiceTest {
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private AvatarService avatarService;
    @Value("${dicebear.api.url}")
    private String dicebearApiUrl = "http://localhost/dicebear";

    @BeforeEach
    public void setUp() {
        avatarService.setDicebearApiUrl(dicebearApiUrl);
    }

    @Test
    void testGenerateRandomAvatarGenerated() {
        String avatarSvg = "<svg>...</svg>";
        when(restTemplate.getForObject(any(String.class), eq(String.class))).thenReturn(avatarSvg);
        String result = avatarService.generateRandomAvatar();
        assertEquals(avatarSvg, result);
    }

    @Test
    void testGenerateRandomAvatarWithAvatarNotGenerated() {
        when(restTemplate.getForObject(any(String.class), eq(String.class))).thenReturn(null);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                avatarService.generateRandomAvatar());
        assertEquals("Could not generate an avatar", exception.getMessage());
    }
}