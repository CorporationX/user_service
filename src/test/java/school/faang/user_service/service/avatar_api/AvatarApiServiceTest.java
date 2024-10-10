package school.faang.user_service.service.avatar_api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class AvatarApiServiceTest {
    private AvatarApiService avatarApiService;
    @Mock
    private RestTemplate restTemplate;
    private String apiUrl = "https://api.dicebear.com/9.x/initials/svg?scale=50&backgroundType=gradientLinear&radius=50&seed=";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        avatarApiService = new AvatarApiService(apiUrl, restTemplate);
    }

    @Test
    @DisplayName("+ API call for default avatar")
    public void generateDefaultAvatarTest() {
        String username = "Alex.Cheremisin";
        String url = apiUrl + username;
        byte[] expected = {1, 2, 3, 4, 5};
        when(restTemplate.getForObject(url, byte[].class)).thenReturn(expected);

        byte[] response = avatarApiService.generateDefaultAvatar(username);

        assertNotNull(response);
        assertEquals(expected, response);
    }

    @Test
    @DisplayName("- API call for default avatar: call failed")
    public void generateDefaultAvatarTest_() {
        assertThrows(RuntimeException.class, () -> avatarApiService.generateDefaultAvatar("Alex.Cheremisin"));
    }
}
