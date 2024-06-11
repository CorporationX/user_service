package school.faang.user_service.service.user.image;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class AvatarGeneratorServiceTest {
    @Spy
    private AvatarGeneratorService avatarGeneratorService;

    @Test
    void getRandomAvatarPositiveTest() {
        ReflectionTestUtils.setField(avatarGeneratorService,
                "baseUrl",
                "https://api.dicebear.com/8.x/notionists/png?size=170&seed=");

        assertDoesNotThrow(() -> avatarGeneratorService.getRandomAvatar());
    }
}