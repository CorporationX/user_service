package school.faang.user_service.service.avatar_generator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.DiceBearAvatarConfig;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DiceBear avatar generator test")
class AvatarGeneratorTest {
    @Mock
    DiceBearAvatarConfig diceBearAvatarConfig;

    @InjectMocks
    AvatarGenerator avatarGenerator;

    @BeforeEach
    void setUp() {
        when(diceBearAvatarConfig.getStyle()).thenReturn("croodles");
        when(diceBearAvatarConfig.getFormat()).thenReturn("svg");
    }

    @Test
    @DisplayName("Static avatar generate test")
    void generateAvatarStaticLink() {
        String link = avatarGenerator.generateAvatarLinkStatic();
        assertTrue(link.startsWith("https://api.dicebear.com/9.x/croodles/svg?seed="), "Wrong start");

        String seed = link.substring(link.lastIndexOf("=") + 1);

        // Check if the seed is one of the enum values
        boolean isValidSeed = false;
        for (DiceBearAvatarSeeds enumValue : DiceBearAvatarSeeds.values()) {
            if (enumValue.name().equals(seed)) {
                isValidSeed = true;
                break;
            }
        }

        // Assert that the seed is a valid enum value
        assertTrue(isValidSeed, "Seed is not a valid enum value");
    }

}